package gov.iti.jets.NutriScan.dto;

import java.time.LocalDate;
import java.util.List;

public record DailyTrackingResponse(Integer id, LocalDate date, Integer targetWaterCnt,
    Integer waterCnt, Integer stepsCnt, Double stepsKcal, Double exerciseKcal, Double exerciseMin,
    Long totalMealKcal, List<DailyTrackingMealResponse> meals) {
}