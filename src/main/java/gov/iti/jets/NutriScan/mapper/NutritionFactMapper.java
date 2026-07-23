package gov.iti.jets.NutriScan.mapper;

import gov.iti.jets.NutriScan.dto.ai.NutritionFactsDto;
import gov.iti.jets.NutriScan.model.NutritionFact;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NutritionFactMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "scans", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    NutritionFact toEntity(NutritionFactsDto dto);

    NutritionFactsDto toDto(NutritionFact entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "scans", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(NutritionFactsDto dto, @MappingTarget NutritionFact entity);
}
