package gov.iti.jets.NutriScan.dto;

import java.time.LocalDate;
import java.util.UUID;

public record DailyTrackingSummaryResponse(Integer id, LocalDate date, Integer targetWaterCnt,
    Integer waterCnt, Integer stepsCnt, Integer mealCount) {
}