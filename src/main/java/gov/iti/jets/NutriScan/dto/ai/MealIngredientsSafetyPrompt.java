package gov.iti.jets.NutriScan.dto.ai;

import java.util.List;

public record MealIngredientsSafetyPrompt(List<String> allergies, List<String> conditions,
    List<FamilyMemberAiRequest> familyMembers) {
}
