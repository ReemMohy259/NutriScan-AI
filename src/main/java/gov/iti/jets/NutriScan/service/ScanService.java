package gov.iti.jets.NutriScan.service;

import gov.iti.jets.NutriScan.dto.ScanResultResponse;
import gov.iti.jets.NutriScan.dto.ScanSubmitResponse;
import gov.iti.jets.NutriScan.dto.ScanSummaryResponse;
import gov.iti.jets.NutriScan.dto.UserAllergiesAndConditionsResponse;
import gov.iti.jets.NutriScan.dto.ai.*;
import gov.iti.jets.NutriScan.exception.ImageUploadException;
import gov.iti.jets.NutriScan.exception.OcrModelException;
import gov.iti.jets.NutriScan.exception.ScanNotFoundException;
import gov.iti.jets.NutriScan.mapper.NutritionFactMapper;
import gov.iti.jets.NutriScan.mapper.ScanMapper;
import gov.iti.jets.NutriScan.model.NutritionFact;
import gov.iti.jets.NutriScan.model.Scan;
import gov.iti.jets.NutriScan.model.ScanFlaggedIngredient;
import gov.iti.jets.NutriScan.model.User;
import gov.iti.jets.NutriScan.repository.ScanRepository;
import gov.iti.jets.NutriScan.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScanService {
    private final AiService aiService;

    private final ScanRepository scanRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ScanMapper scanMapper;
    private final CloudinaryStorageService cloudinaryStorageService;
    private final NutritionFactMapper nutritionFactMapper;

    @Transactional
    public ScanSubmitResponse addNewScan(Jwt jwt, MultipartFile file) {
        UUID userId = UUID.fromString(jwt.getSubject());

        User user = userRepository.getReferenceById(userId);
        Scan scan = new Scan();
        scan.setUser(user);
        scan.setStatus(ScanStatus.PROCESSING);

        try {
            String url = cloudinaryStorageService.upload(file);
            scan.setImageUrl(url);
        } catch (IOException e) {
            throw new ImageUploadException("Failed to upload image");
        }

        Scan scanEntity = scanRepository.save(scan);

        return new ScanSubmitResponse(scanEntity.getId(), scanEntity.getStatus());
    }

    @Async
    @Transactional
    public void processScan(
        Jwt jwt,
        UUID scanId,
        byte[] bytes,
        @Nullable String contentType,
        @Nullable String originalFilename) {

        UUID userId = UUID.fromString(jwt.getSubject());

        OcrResponseDto ocrResponse;
        Scan scan = scanRepository.findById(scanId)
            .orElseThrow(() -> new ScanNotFoundException("Scan not found with id: " + scanId));

        try {
            ocrResponse = aiService.checkImage(bytes, contentType, originalFilename);
            System.out.println(ocrResponse);

        } catch (OcrModelException e) {
            System.out.println("OCR model error:");
            System.out.println(e.getMessage());
            scan.setStatus(ScanStatus.FAILED);
            return;
        }

        if (!ocrResponse.isRelevant() || !ocrResponse.isFoodProduct()) {
            System.out.println("Image is not relevant");
            scan.setStatus(ScanStatus.FAILED);
            return;
            // throw new BusinessException("Image is not relevant");
        }
        UserAllergiesAndConditionsResponse userData;

        if (ocrResponse.isMeal()) {
            try {
                userData = userService.getUserAllergiesAndConditions(userId);
                MealFoodSafetyResponse response = aiService.mealCheckSafety(
                    bytes,
                    contentType,
                    new MealIngredientsSafetyPrompt(
                        userData.getAllergies(),
                        userData.getDiseases()));
                System.out.println(response);
                updateCompletedScan(
                    ocrResponse,
                    scan,
                    response.foodSafetyResponse(),
                    response.nutritionFacts());
            } catch (Exception e) {
                System.out.println("meal check model error:");
                scan.setStatus(ScanStatus.FAILED);
            }
            return;
        }

        List<String> ingredients = null;

        if (ocrResponse.isNeedSearch()) {
            // TODO: implement search method
            try {
                // ingredients = aiService.searchModelGemini(ocrResponse.getSearchQuery(),
                // ocrResponse.getProductName());
                // ingredients = aiService.searchForIngredients(ocrResponse.getSearchQuery());
            } catch (Exception e) {
                // e.printStackTrace();
                // System.out.println("search model error:");
                // System.out.println(e.getMessage());
                // scan.setStatus(ScanStatus.FAILED);
                // return;
            }
        } else {
            ingredients = ocrResponse.getIngredients();
        }

        if (ingredients == null || ingredients.isEmpty()) {
            System.out.println("Failed to parse ingredients.");
            scan.setStatus(ScanStatus.FAILED);
            return;
            // throw new IngredientParsingException("Failed to parse ingredients.");
        }

        userData = userService.getUserAllergiesAndConditions(userId);

        FoodSafetyResponse result;

        try {
            result = aiService.checkSafety(
                new IngredientsSafetyPrompt(
                    ingredients,
                    userData.getAllergies(),
                    userData.getDiseases()));

        } catch (Exception e) {
            System.out.println("check safety model error:");
            System.out.println(e.getMessage());
            scan.setStatus(ScanStatus.FAILED);
            return;
        }

        updateCompletedScan(ocrResponse, scan, result, ocrResponse.getNutritionFacts());
    }

    private void updateCompletedScan(
        OcrResponseDto ocrResponse,
        Scan scan,
        FoodSafetyResponse response,
        NutritionFactsDto nutritionFacts) {
        scan.setProductName(ocrResponse.getProductName());
        scan.setStatus(ScanStatus.COMPLETED);
        scan.setVerdict(response.verdict());
        scan.setSummary(response.summary());
        scan.getScanFlaggedIngredients().clear();

        System.out.println(nutritionFacts);

        if (nutritionFacts != null) {
            NutritionFact nutritionFact = nutritionFactMapper.toEntity(nutritionFacts);
            nutritionFact.setScans(scan);
            scan.setNutritionFact(nutritionFact);
        }

        response.flaggedIngredients()
            .forEach(
                flaggedIngredient -> scan.addFlaggedIngredient(
                    ScanFlaggedIngredient.builder()
                        .conditionName(String.join(", ", flaggedIngredient.name()))
                        .ingredientName(flaggedIngredient.ingredient())
                        .type(flaggedIngredient.type().name())
                        .reason(flaggedIngredient.reason())
                        .build()));
    }

    public ScanResultResponse findById(UUID id, Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());

        return scanRepository.findByIdWithDetails(id, userId)
            .map(scanMapper::toResultResponse)
            .orElseThrow(() -> new ScanNotFoundException("Scan not found with id: " + id));
    }

    // Careful for N+1 queries
    public Page<ScanSummaryResponse> findByUserId(Jwt jwt, Pageable pageable) {
        UUID userId = UUID.fromString(jwt.getSubject());

        return scanRepository.findSummaryByUserId(userId, pageable);
    }

    public void delete(UUID id) {
        scanRepository.deleteById(id);
    }
}
