package gov.iti.jets.NutriScan.mapper;

import gov.iti.jets.NutriScan.dto.DailyTrackingMealResponse;
import gov.iti.jets.NutriScan.dto.DailyTrackingRequest;
import gov.iti.jets.NutriScan.dto.DailyTrackingResponse;
import gov.iti.jets.NutriScan.dto.DailyTrackingSummaryResponse;
import gov.iti.jets.NutriScan.model.DailyTracking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = DailyTrackingMealMapper.class)
public interface DailyTrackingMapper {

    @Mapping(target = "meals", source = "meals")
    DailyTrackingResponse toResponse(DailyTracking entity);

    DailyTrackingSummaryResponse toSummaryResponse(DailyTracking entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "date", source = "date")
    @Mapping(target = "targetWaterCnt", source = "targetWaterCnt")
    @Mapping(target = "waterCnt", source = "waterCnt")
    @Mapping(target = "stepsCnt", source = "stepsCnt")
    @Mapping(target = "meals", ignore = true)
    void updateFromRequest(DailyTrackingRequest request, @MappingTarget DailyTracking entity);
}