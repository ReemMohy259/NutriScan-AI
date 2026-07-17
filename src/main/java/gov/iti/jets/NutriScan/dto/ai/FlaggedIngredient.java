package gov.iti.jets.NutriScan.dto.ai;

import java.util.List;

public record FlaggedIngredient(String ingredient, String reason, FlagType type,
    List<String> name) {
    public enum FlagType {
        ALLERGY, CHRONIC_CONDITION
    }
}
