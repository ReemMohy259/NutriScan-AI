package gov.iti.jets.NutriScan.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record DailyTrackingRequest(
    @Min(value = 1, message = "Target water count must be at least 1") @Max(value = 50, message = "Target water count must not exceed 50") Integer targetWaterCnt,
    @Min(value = 0, message = "Water count must be non-negative") @Max(value = 50, message = "Water count must not exceed 50") Integer waterCnt,
    @Min(value = 0, message = "Steps count must be non-negative") @Max(value = 200000, message = "Steps count must not exceed 200000") Integer stepsCnt,
    @Min(value = 0, message = "Steps calories must be non-negative") Double stepsKcal,
    @Min(value = 0, message = "Exercise calories must be non-negative") Double exerciseKcal,
    @Min(value = 0, message = "Exercise minutes must be non-negative") Double exerciseMin) {
}