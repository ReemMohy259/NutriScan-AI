package gov.iti.jets.NutriScan.service;

import gov.iti.jets.NutriScan.dto.*;
import gov.iti.jets.NutriScan.dto.ai.*;
import gov.iti.jets.NutriScan.exception.*;
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
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;

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

    @Async("asyncExecutor")
    @Transactional
    public void processScan(
        Jwt jwt,
        UUID scanId,
        byte[] bytes,
        @Nullable String contentType,
        @Nullable String originalFilename) {

        UUID userId = UUID.fromString(jwt.getSubject());

        Scan scan = scanRepository.findById(scanId)
            .orElseThrow(() -> new ScanNotFoundException("Scan not found with id: " + scanId));

        try {
            OcrResponseDto ocrResponse = aiService.checkImage(bytes, contentType, originalFilename);

            System.out.println(ocrResponse);

            if (!ocrResponse.isRelevant() || !ocrResponse.isFoodProduct())
                throw new BusinessException("Image is not relevant");

            if (ocrResponse.isBlurry())
                throw new ImageTooBlurry("Image is blurry please take a clearer picture");

            if (ocrResponse.isMeal()) {
                UserAllergiesAndConditionsResponse userData = userService
                    .getUserAllergiesAndConditions(userId);

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
                    response.nutritionFacts(),
                    userId);

                return;
            }

            List<String> ingredients = null;

            if (ocrResponse.isNeedSearch()) {
                ingredients = aiService.searchForIngredientsModel(
                    ocrResponse.getSearchQuery(),
                    ocrResponse.getProductName());
            } else {
                ingredients = ocrResponse.getIngredients();
            }

            if (ingredients == null || ingredients.isEmpty())
                throw new IngredientParsingException("Failed to parse ingredients.");

            UserAllergiesAndConditionsResponse userData = userService
                .getUserAllergiesAndConditions(userId);

            FoodSafetyResponse result = aiService.checkSafety(
                new IngredientsSafetyPrompt(
                    ingredients,
                    userData.getAllergies(),
                    userData.getDiseases()));

            updateCompletedScan(ocrResponse, scan, result, ocrResponse.getNutritionFacts(), userId);

        } catch (MealModelException e) {
            System.out.println("Meal model error:");
            System.out.println(e.getMessage());
            scan.setStatus(ScanStatus.FAILED);

            eventPublisher
                .publishEvent(new ScanStatusChangedEvent(userId, scan.getId(), ScanStatus.FAILED));
        } catch (OcrModelException e) {
            System.out.println("OCR model error:");
            System.out.println(e.getMessage());
            scan.setStatus(ScanStatus.FAILED);

            eventPublisher
                .publishEvent(new ScanStatusChangedEvent(userId, scan.getId(), ScanStatus.FAILED));
        } catch (BusinessException e) {
            System.out.println("Image is not relevant:");
            System.out.println(e.getMessage());
            scan.setStatus(ScanStatus.FAILED);

            eventPublisher
                .publishEvent(new ScanStatusChangedEvent(userId, scan.getId(), ScanStatus.FAILED));
        } catch (IngredientParsingException e) {
            System.out.println("Failed to parse ingredients:");
            System.out.println(e.getMessage());
            scan.setStatus(ScanStatus.FAILED);

            eventPublisher
                .publishEvent(new ScanStatusChangedEvent(userId, scan.getId(), ScanStatus.FAILED));
        } catch (Exception e) {
            System.out.println("Processing error:");
            System.out.println(e.getMessage());
            scan.setStatus(ScanStatus.FAILED);

            eventPublisher
                .publishEvent(new ScanStatusChangedEvent(userId, scan.getId(), ScanStatus.FAILED));
        }
    }

    private void updateCompletedScan(
        OcrResponseDto ocrResponse,
        Scan scan,
        FoodSafetyResponse response,
        NutritionFactsDto nutritionFacts,
        UUID userId) {

        System.out.println("--------------------------------------------------------");
        System.out.println(response);
        System.out.println(nutritionFacts);
        System.out.println("--------------------------------------------------------");

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

        eventPublisher
            .publishEvent(new ScanStatusChangedEvent(userId, scan.getId(), ScanStatus.COMPLETED));
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
