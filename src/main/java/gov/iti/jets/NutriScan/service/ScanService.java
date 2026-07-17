package gov.iti.jets.NutriScan.service;

import gov.iti.jets.NutriScan.dto.ScanSubmitResponse;
import gov.iti.jets.NutriScan.dto.ai.FoodSafetyResponse;
import gov.iti.jets.NutriScan.dto.ai.IngredientsSafetyPrompt;
import gov.iti.jets.NutriScan.dto.ai.OcrResponseDto;
import gov.iti.jets.NutriScan.exception.BusinessException;
import gov.iti.jets.NutriScan.exception.ImageTooLargeException;
import gov.iti.jets.NutriScan.exception.IngredientParsingException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ScanService {
    private final AiService aiService;

    private static final long MAX_IMAGE_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB

    public ScanService(AiService aiService) {
        this.aiService = aiService;
    }

    // i guess
    // @Async
    public void processScan(MultipartFile image) {

        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/"))
            throw new IllegalArgumentException("Only image files are allowed");

        if (image.getSize() > MAX_IMAGE_SIZE_BYTES)
            throw new ImageTooLargeException("Image size must not exceed 5 MB");

        OcrResponseDto ocrResponse = aiService.checkImage(image);

        if (!ocrResponse.isRelevant() || !ocrResponse.isFoodProduct())
            throw new BusinessException("Image is not relevant");

        List<String> ingredients = null;

        if (ocrResponse.isNeedSearch()) {
            // TODO: implement search method
            // ingredients = aiService.searchForIngredients(ocrResponse.getSearchQuery());
        } else {
            ingredients = ocrResponse.getIngredients();
        }

        if (ingredients == null || ingredients.isEmpty())
            throw new IngredientParsingException("Failed to parse ingredients.");

        // TODO: Add user allergies and conditions
        FoodSafetyResponse result = aiService
            .checkSafety(new IngredientsSafetyPrompt(ingredients, List.of(), List.of()));

    }
    public ScanSubmitResponse addNewScan() {
        // TODO: get id from token and add repository layer
        return new ScanSubmitResponse(null, null);
    }
}
