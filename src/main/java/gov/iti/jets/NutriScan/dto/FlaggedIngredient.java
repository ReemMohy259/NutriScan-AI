package gov.iti.jets.NutriScan.dto;

import java.util.List;

public record FlaggedIngredient(String ingredient, String reason, FlagType type,
    List<String> name) {
    public enum FlagType {
        ALLERGY, CONDITION
    }
}
