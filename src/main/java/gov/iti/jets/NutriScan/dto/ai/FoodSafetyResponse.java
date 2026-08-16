package gov.iti.jets.NutriScan.dto.ai;

import java.util.List;

public record FoodSafetyResponse(Verdict verdict, List<FlaggedIngredient> flaggedIngredients,
    String summary, List<FamilyAlertDto> familyAlerts) {
}