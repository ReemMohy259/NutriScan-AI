package gov.iti.jets.NutriScan.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record DailyTrackingResponse(Integer id, LocalDate date, Integer targetWaterCnt,
    Integer waterCnt, Integer stepsCnt, List<DailyTrackingMealResponse> meals) {
}