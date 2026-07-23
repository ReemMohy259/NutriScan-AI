package gov.iti.jets.NutriScan.dto.ai;

import java.math.BigDecimal;

public record NutritionFactsDto(Integer calories, BigDecimal proteinGrams, BigDecimal carbsGrams,
    BigDecimal fatG, BigDecimal fiberGrams, BigDecimal sugarG, BigDecimal sodiumMg) {
}