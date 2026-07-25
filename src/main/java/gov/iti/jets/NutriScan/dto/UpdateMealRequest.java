package gov.iti.jets.NutriScan.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateMealRequest(
    @Min(message = "Meal count must be at least 1", value = 1) @NotNull Integer mealCnt) {
}
