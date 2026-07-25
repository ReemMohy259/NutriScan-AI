package gov.iti.jets.NutriScan.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

import java.util.UUID;

public record DailyTrackingMealRequest(@NotNull UUID scanId,
    @NotNull @Min(value = 1, message = "Meal count must be at least 1") Integer mealCnt) {
}