package gov.iti.jets.NutriScan.mapper;

import gov.iti.jets.NutriScan.dto.DailyTrackingResponse;
import gov.iti.jets.NutriScan.dto.DailyTrackingSummaryResponse;
import gov.iti.jets.NutriScan.model.DailyTracking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = DailyTrackingMealMapper.class)
public interface DailyTrackingMapper {

    @Mapping(target = "meals", source = "meals")
    @Mapping(target = "totalMealKcal", expression = "java(calculateTotalMealKcal(entity))")
    DailyTrackingResponse toResponse(DailyTracking entity);

    @Mapping(target = "totalMealKcal", expression = "java(calculateTotalMealKcal(entity))")
    @Mapping(target = "mealCount", expression = "java(entity.getMeals() == null ? 0 : entity.getMeals().size())")
    DailyTrackingSummaryResponse toSummaryResponse(DailyTracking entity);

    default Long calculateTotalMealKcal(DailyTracking entity) {
        if (entity == null || entity.getMeals() == null) {
            return 0L;
        }

        return entity.getMeals()
            .stream()
            .filter(meal -> meal.getMealCnt() != null)
            .filter(meal -> meal.getScan() != null)
            .filter(meal -> meal.getScan().getNutritionFact() != null)
            .filter(meal -> meal.getScan().getNutritionFact().getCalories() != null)
            .mapToLong(
                meal -> (long) meal.getMealCnt() * meal.getScan().getNutritionFact().getCalories())
            .sum();
    }
}