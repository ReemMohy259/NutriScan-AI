package gov.iti.jets.NutriScan.service;

import gov.iti.jets.NutriScan.dto.*;
import gov.iti.jets.NutriScan.dto.ai.*;
import gov.iti.jets.NutriScan.dto.ai.barcode.BarCodeProductDto;
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
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
@Slf4j
public class ScanService {
    private final AiService aiService;

    private final ScanRepository scanRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ScanMapper scanMapper;
    private final CloudinaryStorageService cloudinaryStorageService;
    private final NutritionFactMapper nutritionFactMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final OpenFoodFactsService openFoodFactsService;
    private final CacheManager cacheManager;

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

    @Transactional
    public ScanSubmitResponse addNewBarcodeScan(Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());

        User user = userRepository.getReferenceById(userId);
        Scan scan = new Scan();
        scan.setUser(user);
        scan.setStatus(ScanStatus.PROCESSING);

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

        long startTime = System.nanoTime();

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
                long endTime = System.nanoTime();
                long durationInMilliseconds = (endTime - startTime) / 1_000_000;
                System.out.println("Execution time for ai flow: " + durationInMilliseconds + " ms");
                System.out.println("--------------------------------------------------------");

                updateCompletedScan(
                    ocrResponse.getProductName(),
                    scan,
                    response.foodSafetyResponse(),
                    response.nutritionFacts(),
                    userId,
                    null);

                return;
            }

            List<String> ingredients;

            if (ocrResponse.isNeedSearch()) {
                SearchModelResponseDto response = aiService.searchForIngredientsModel(
                    ocrResponse.getSearchQuery(),
                    ocrResponse.getProductName());
                ingredients = response.ingredients();
                if (response.nutritionFacts() != null && !response.nutritionFactsAreEmpty())
                    ocrResponse.setNutritionFacts(response.nutritionFacts());

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

            updateCompletedScan(
                ocrResponse.getProductName(),
                scan,
                result,
                ocrResponse.getNutritionFacts(),
                userId,
                null);

            long endTime = System.nanoTime();
            long durationInMilliseconds = (endTime - startTime) / 1_000_000;

            System.out.println("Execution time for ai flow: " + durationInMilliseconds + " ms");
            System.out.println("--------------------------------------------------------");
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

    @Async("asyncExecutor")
    @Transactional
    public void processBarcodeScan(Jwt jwt, UUID scanId, String barcode) {

        long startTime = System.nanoTime();

        UUID userId = UUID.fromString(jwt.getSubject());

        log.info("Starting barcode scan processing for scanId: {}, barcode: {}", scanId, barcode);

        Scan scan = scanRepository.findById(scanId)
            .orElseThrow(() -> new ScanNotFoundException("Scan not found with id: " + scanId));

        try {
            log.debug("Fetching product from OpenFoodFacts for barcode: {}", barcode);
            BarCodeProductDto product = openFoodFactsService.getProduct(barcode);
            log.info("Product fetched: {}", product.getProductName());

            log.debug("Extracting ingredients");
            List<String> ingredients = openFoodFactsService.extractIngredients(product);
            log.info("Extracted {} ingredients: {}", ingredients.size(), ingredients);
            if (ingredients.isEmpty()) {
                log.warn("No ingredients found for product: {}", product.getProductName());
                throw new IngredientParsingException("No ingredients found for this product");
            }

            log.debug("Extracting nutrition facts");
            NutritionFactsDto nutritionFacts = openFoodFactsService.extractNutritionFacts(product);
            log.info("Nutrition facts extracted: {}", nutritionFacts);

            log.debug("Extracting allergens");
            List<String> allergens = openFoodFactsService.extractAllergens(product);

            List<String> traces = openFoodFactsService.extractTraces(product);
            log.debug("Fetching user allergies and conditions");
            UserAllergiesAndConditionsResponse userData = userService
                .getUserAllergiesAndConditions(userId);
            log.debug(
                "User allergies: {}, conditions: {}",
                userData.getAllergies(),
                userData.getDiseases());

            log.debug("Calling AI safety check");
            FoodSafetyResponse result = aiService.checkBarcodeSafety(
                new BarCodeSafetyPrompt(
                    barcode,
                    product.getProductName(),
                    product.getCategoriesTags(),
                    ingredients,
                    userData.getAllergies(),
                    userData.getDiseases(),
                    allergens,
                    traces));
            log.info(
                "AI safety check completed: verdict={}, flaggedCount={}",
                result.verdict(),
                result.flaggedIngredients().size());

            updateCompletedScan(
                product.getProductName(),
                scan,
                result,
                nutritionFacts,
                userId,
                product.getImageUrl());

            long endTime = System.nanoTime();
            long durationInMilliseconds = (endTime - startTime) / 1_000_000;
            log.info(
                "Barcode scan completed in {} ms for scanId: {}",
                durationInMilliseconds,
                scanId);
        } catch (BarCodeNotFoundException e) {
            log.warn("Barcode not found: {}", e.getMessage());
            scan.setStatus(ScanStatus.FAILED);

            eventPublisher
                .publishEvent(new ScanStatusChangedEvent(userId, scan.getId(), ScanStatus.FAILED));
        } catch (IngredientParsingException e) {
            log.warn("Failed to parse ingredients: {}", e.getMessage());
            scan.setStatus(ScanStatus.FAILED);

            eventPublisher
                .publishEvent(new ScanStatusChangedEvent(userId, scan.getId(), ScanStatus.FAILED));
        } catch (Exception e) {
            log.error("Processing error for scanId {}: {}", scanId, e.getMessage(), e);
            scan.setStatus(ScanStatus.FAILED);

            eventPublisher
                .publishEvent(new ScanStatusChangedEvent(userId, scan.getId(), ScanStatus.FAILED));
        }
    }

    // caching inside the method body
    private void updateCompletedScan(
        String productName,
        Scan scan,
        FoodSafetyResponse response,
        NutritionFactsDto nutritionFacts,
        UUID userId,
        String imageUrl) {

        System.out.println("--------------------------------------------------------");
        System.out.println(response);
        System.out.println(nutritionFacts);
        System.out.println("--------------------------------------------------------");

        scan.setProductName(productName);
        scan.setStatus(ScanStatus.COMPLETED);
        scan.setVerdict(response.verdict());
        scan.setSummary(response.summary());
        scan.getScanFlaggedIngredients().clear();

        if (imageUrl != null) {
            scan.setImageUrl(imageUrl);
        }

        System.out.println(nutritionFacts);

        if (!nutritionFacts.isEmpty()) {
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

        Cache cache = cacheManager.getCache("scans");
        if (cache != null) {
            cache.put(
                scan.getId().toString() + ':' + userId.toString(),
                scanMapper.toResultResponse(scan));
        }

        eventPublisher
            .publishEvent(new ScanStatusChangedEvent(userId, scan.getId(), ScanStatus.COMPLETED));
    }

    @Transactional
    @CachePut(value = "scans", key = "#scanId.toString() + ':' + #jwt.getSubject()")
    public ScanResultResponse updateScan(UUID scanId, String name, Boolean isFavorite, Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        Scan scan = scanRepository.findByIdWithDetails(scanId, userId)
            .orElseThrow(() -> new ScanNotFoundException("Scan not found with id: " + scanId));
        if (name != null)
            scan.setProductName(name);

        if (isFavorite != null)
            scan.setFavorite(isFavorite);

        return scanMapper.toResultResponse(scan);
    }
    @Cacheable(value = "scans", key = "#id.toString() + ':' + #jwt.getSubject()", unless = "#result.status() == T(gov.iti.jets.NutriScan.dto.ai.ScanStatus).PROCESSING")
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

    // Careful for N+1 queries
    public Page<ScanSummaryResponse> findFavoritesByUserId(Jwt jwt, Pageable pageable) {
        UUID userId = UUID.fromString(jwt.getSubject());

        return scanRepository.findFavoritesByUserId(userId, pageable);
    }

    @CacheEvict(value = "scans", key = "#id.toString() + ':' + #jwt.getSubject()")
    public void delete(UUID id, Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());

        scanRepository.deleteByIdAndUserId(id, userId);
    }
}
