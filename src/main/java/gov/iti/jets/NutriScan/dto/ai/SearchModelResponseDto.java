package gov.iti.jets.NutriScan.dto.ai;

import java.util.List;

public record SearchModelResponseDto(List<String> ingredients, NutritionFactsDto nutritionFacts) {
    
    public boolean nutritionFactsAreEmpty() {
        return nutritionFacts.calories() == 0
                && nutritionFacts.carbsGrams().compareTo(java.math.BigDecimal.ZERO) == 0
                && nutritionFacts.proteinGrams().compareTo(java.math.BigDecimal.ZERO) == 0
                && nutritionFacts.fatG().compareTo(java.math.BigDecimal.ZERO) == 0
                && nutritionFacts.fiberGrams().compareTo(java.math.BigDecimal.ZERO) == 0
                && nutritionFacts.sugarG().compareTo(java.math.BigDecimal.ZERO) == 0
                && nutritionFacts.sodiumMg().compareTo(java.math.BigDecimal.ZERO) == 0;
    }
}
