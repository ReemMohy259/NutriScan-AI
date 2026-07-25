package gov.iti.jets.NutriScan.mapper;

import gov.iti.jets.NutriScan.dto.DailyTrackingMealResponse;
import gov.iti.jets.NutriScan.model.DailyTrackingMeal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DailyTrackingMealMapper {

    @Mapping(source = "id.scanId", target = "scanId")
    @Mapping(source = "scan.productName", target = "productName")
    @Mapping(source = "scan.imageUrl", target = "imageUrl")
    @Mapping(source = "mealCnt", target = "mealCnt")
    @Mapping(source = "scan.nutritionFact", target = "nutritionFacts")
    DailyTrackingMealResponse toResponse(DailyTrackingMeal entity);
}