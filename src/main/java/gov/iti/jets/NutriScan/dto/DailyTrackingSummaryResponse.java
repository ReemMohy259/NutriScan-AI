package gov.iti.jets.NutriScan.dto;

import java.time.LocalDate;

public record DailyTrackingSummaryResponse(Integer id, LocalDate date, Integer targetWaterCnt,
    Integer waterCnt, Integer stepsCnt, Double stepsKcal, Double exerciseKcal, Double exerciseMin,
    Long totalMealKcal, Integer mealCount) {
}