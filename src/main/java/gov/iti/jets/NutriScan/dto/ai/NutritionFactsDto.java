package gov.iti.jets.NutriScan.dto.ai;

import java.math.BigDecimal;

public record NutritionFactsDto(Integer calories, BigDecimal proteinGrams, BigDecimal carbsGrams,
    BigDecimal fatG, BigDecimal fiberGrams, BigDecimal sugarG, BigDecimal sodiumMg) {

    public boolean isEmpty() {
        return isZero(calories) && isZero(proteinGrams) && isZero(carbsGrams) && isZero(fatG)
            && isZero(fiberGrams) && isZero(sugarG) && isZero(sodiumMg);
    }

    private static boolean isZero(Integer value) {
        return value == null || value == 0;
    }

    private static boolean isZero(BigDecimal value) {
        return value == null || value.compareTo(BigDecimal.ZERO) == 0;
    }

}