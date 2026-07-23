package gov.iti.jets.NutriScan.dto.ai;

public record MealFoodSafetyResponse(FoodSafetyResponse foodSafetyResponse,
    NutritionFactsDto nutritionFacts) {
}
