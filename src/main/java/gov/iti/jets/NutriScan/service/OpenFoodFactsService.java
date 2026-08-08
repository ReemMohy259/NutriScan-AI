package gov.iti.jets.NutriScan.service;

import gov.iti.jets.NutriScan.dto.ai.NutritionFactsDto;
import gov.iti.jets.NutriScan.dto.ai.barcode.BarCodeNutrimentsDto;
import gov.iti.jets.NutriScan.dto.ai.barcode.BarCodeProductDto;
import gov.iti.jets.NutriScan.dto.ai.barcode.BarCodeResponseDto;
import gov.iti.jets.NutriScan.exception.BarCodeNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenFoodFactsService {

    private final RestTemplate restTemplate;

    private static final String URL = "https://world.openfoodfacts.org/api/v2/product/%s.json";

    @Cacheable(value = "openFoodFacts", key = "#barcode", unless = "#result == null")
    public BarCodeProductDto getProduct(String barcode) {

        try {
            BarCodeResponseDto response = restTemplate
                .getForObject(String.format(URL, barcode), BarCodeResponseDto.class);

            if (response == null || response.getStatus() == 0 || response.getProduct() == null) {
                throw new BarCodeNotFoundException("No product found for barcode: " + barcode);
            }

            BarCodeProductDto product = response.getProduct();
            log.info("=== OpenFoodFacts Product Fetch ===");
            log.info("Product Name: {}", product.getProductName());
            log.info("Barcode: {}", barcode);
            log.info("Brands: {}", product.getBrands());
            log.info("Product Type: {}", product.getProductType());
            log.info("Categories Tags: {}", product.getCategoriesTags());
            log.info("Food Groups: {}", product.getFoodGroups());
            log.info("Ingredients Text: {}", product.getIngredientsText());
            log.info("Ingredients Tags: {}", product.getIngredientsTags());
            log.info("Allergens Tags: {}", product.getAllergensTags());
            log.info("Traces Tags: {}", product.getTracesTags());
            log.info("Nova Group: {}", product.getNovaGroup());
            log.info("Image URL: {}", product.getImageUrl());
            if (product.getNutriments() != null) {
                log.info(
                    "Nutriments: energy_kcal_100g={}, proteins_100g={}, carbohydrates_100g={}, fat_100g={}, fiber_100g={}, sugars_100g={}, sodium_100g={}, salt_100g={}, saturated_fat_100g={}",
                    product.getNutriments().getEnergyKcal100g(),
                    product.getNutriments().getProteins100g(),
                    product.getNutriments().getCarbohydrates100g(),
                    product.getNutriments().getFat100g(),
                    product.getNutriments().getFiber100g(),
                    product.getNutriments().getSugars100g(),
                    product.getNutriments().getSodium100g(),
                    product.getNutriments().getSalt100g(),
                    product.getNutriments().getSaturatedFat100g());
            } else {
                log.info("Nutriments: null");
            }
            log.info("====================================");

            return product;

        } catch (HttpClientErrorException.NotFound ex) {
            log.warn("Product not found for barcode: {}", barcode);
            throw new BarCodeNotFoundException("No product found for barcode: " + barcode);
        }
    }

    public List<String> extractIngredients(BarCodeProductDto product) {
        if (product.getIngredientsTags() != null && !product.getIngredientsTags().isEmpty()) {
            List<String> ingredients = product.getIngredientsTags()
                .stream()
                .map(tag -> tag.replace("en:", "").replace("-", " "))
                .collect(Collectors.toList());
            log.info("Extracted {} ingredients from tags: {}", ingredients.size(), ingredients);
            return ingredients;
        }

        if (product.getIngredientsText() != null && !product.getIngredientsText().isBlank()) {
            List<String> ingredients = Arrays.stream(product.getIngredientsText().split("[,;]"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
            log.info("Extracted {} ingredients from text: {}", ingredients.size(), ingredients);
            return ingredients;
        }

        log.warn("No ingredients found for product: {}", product.getProductName());
        return List.of();
    }

    public NutritionFactsDto extractNutritionFacts(BarCodeProductDto product) {
        BarCodeNutrimentsDto nutriments = product.getNutriments();
        if (nutriments == null) {
            log.info("No nutriments data for product: {}", product.getProductName());
            return null;
        }

        Integer calories = nutriments.getEnergyKcal100g() != null
            ? nutriments.getEnergyKcal100g().intValue()
            : null;
        BigDecimal proteinGrams = nutriments.getProteins100g() != null
            ? BigDecimal.valueOf(nutriments.getProteins100g())
            : null;
        BigDecimal carbsGrams = nutriments.getCarbohydrates100g() != null
            ? BigDecimal.valueOf(nutriments.getCarbohydrates100g())
            : null;
        BigDecimal fatG = nutriments.getFat100g() != null
            ? BigDecimal.valueOf(nutriments.getFat100g())
            : null;
        BigDecimal fiberGrams = nutriments.getFiber100g() != null
            ? BigDecimal.valueOf(nutriments.getFiber100g())
            : null;
        BigDecimal sugarG = nutriments.getSugars100g() != null
            ? BigDecimal.valueOf(nutriments.getSugars100g())
            : null;
        BigDecimal sodiumMg = null;
        if (nutriments.getSodium100g() != null) {
            sodiumMg = BigDecimal.valueOf(nutriments.getSodium100g() * 1000);
        } else if (nutriments.getSalt100g() != null) {
            sodiumMg = BigDecimal.valueOf(nutriments.getSalt100g() * 1000 * 0.4);
        }

        NutritionFactsDto dto = new NutritionFactsDto(
            calories,
            proteinGrams,
            carbsGrams,
            fatG,
            fiberGrams,
            sugarG,
            sodiumMg);
        log.info(
            "Extracted nutrition facts (per 100g): calories={}, protein={}g, carbs={}g, fat={}g, fiber={}g, sugar={}g, sodium={}mg",
            calories,
            proteinGrams,
            carbsGrams,
            fatG,
            fiberGrams,
            sugarG,
            sodiumMg);
        return dto;
    }
    public List<String> extractAllergens(BarCodeProductDto product) {
        if (product.getAllergensTags() != null && !product.getAllergensTags().isEmpty()) {

            List<String> allergens = product.getAllergensTags()
                .stream()
                .map(tag -> tag.replace("en:", "").replace("-", " "))
                .map(String::trim)
                .collect(Collectors.toList());

            log.info("Extracted {} allergens: {}", allergens.size(), allergens);
            return allergens;
        }

        log.info("No allergens found for product: {}", product.getProductName());
        return List.of();
    }

    public List<String> extractTraces(BarCodeProductDto product) {
        if (product.getTracesTags() == null) {
            return List.of();
        }

        return product.getTracesTags()
            .stream()
            .map(tag -> tag.replace("en:", "").replace("-", " "))
            .toList();
    }

}
