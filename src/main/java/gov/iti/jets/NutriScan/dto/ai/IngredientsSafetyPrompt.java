package gov.iti.jets.NutriScan.dto.ai;

import java.util.List;

public record IngredientsSafetyPrompt(List<String> ingredients, List<String> allergies,
    List<String> conditions, List<FamilyMemberAiRequest> familyMembers) {
}
