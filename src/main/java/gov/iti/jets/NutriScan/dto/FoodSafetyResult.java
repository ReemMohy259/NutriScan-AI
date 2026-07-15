package gov.iti.jets.NutriScan.dto;

import java.util.List;

public record FoodSafetyResult(
        Verdict verdict,
        List<FlaggedIngredient> flaggedIngredients,
        String summary
) {}