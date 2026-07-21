package gov.iti.jets.NutriScan.service;

import gov.iti.jets.NutriScan.dto.ScanResultResponse;
import gov.iti.jets.NutriScan.dto.ScanSubmitResponse;
import gov.iti.jets.NutriScan.dto.ScanSummaryResponse;
import gov.iti.jets.NutriScan.dto.ai.FoodSafetyResponse;
import gov.iti.jets.NutriScan.dto.ai.IngredientsSafetyPrompt;
import gov.iti.jets.NutriScan.dto.ai.OcrResponseDto;
import gov.iti.jets.NutriScan.dto.ai.ScanStatus;
import gov.iti.jets.NutriScan.exception.BusinessException;
import gov.iti.jets.NutriScan.exception.ImageUploadException;
import gov.iti.jets.NutriScan.exception.IngredientParsingException;
import gov.iti.jets.NutriScan.exception.ScanNotFoundException;
import gov.iti.jets.NutriScan.mapper.ScanMapper;
import gov.iti.jets.NutriScan.model.Scan;
import gov.iti.jets.NutriScan.model.User;
import gov.iti.jets.NutriScan.repository.ScanRepository;
import gov.iti.jets.NutriScan.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    private static final long MAX_IMAGE_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB

    private final ScanRepository scanRepository;
    private final UserRepository userRepository;
    private final ScanMapper scanMapper;
    private final CloudinaryStorageService cloudinaryStorageService;

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
            e.printStackTrace();
            throw new ImageUploadException("Failed to upload image");
        }

        Scan scanEntity = scanRepository.save(scan);

        return new ScanSubmitResponse(scanEntity.getId(), scanEntity.getStatus());
    }

    // @Async
    public void processScan(MultipartFile image) {

        OcrResponseDto ocrResponse = aiService.checkImage(image);

        if (!ocrResponse.isRelevant() || !ocrResponse.isFoodProduct())
            throw new BusinessException("Image is not relevant");

        List<String> ingredients = null;

        if (ocrResponse.isNeedSearch()) {
            //TODO: implement search method
            //ingredients = aiService.searchForIngredients(ocrResponse.getSearchQuery());
        } else {
            ingredients = ocrResponse.getIngredients();
        }

        if (ingredients == null || ingredients.isEmpty())
            throw new IngredientParsingException("Failed to parse ingredients.");

        //TODO: Add user allergies and conditions
        FoodSafetyResponse result = aiService.checkSafety(
                new IngredientsSafetyPrompt(
                        ingredients,
                        List.of(),
                        List.of()
                )
        );
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
