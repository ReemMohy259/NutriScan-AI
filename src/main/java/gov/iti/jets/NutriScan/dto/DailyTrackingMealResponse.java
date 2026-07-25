package gov.iti.jets.NutriScan.dto;

import gov.iti.jets.NutriScan.dto.ai.NutritionFactsDto;

import java.util.UUID;

public record DailyTrackingMealResponse(UUID scanId, String productName, String imageUrl,
    Integer mealCnt, NutritionFactsDto nutritionFacts) {
}