package gov.iti.jets.NutriScan.mapper;

import gov.iti.jets.NutriScan.dto.DiseaseRequest;
import gov.iti.jets.NutriScan.dto.DiseaseResponse;
import gov.iti.jets.NutriScan.model.Disease;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DiseaseMapper {

    @Mapping(target = "id", ignore = true)
    Disease toEntity(DiseaseRequest request);

    DiseaseResponse toResponse(Disease entity);

    List<Disease> toEntityList(List<DiseaseRequest> requests);

    List<DiseaseResponse> toResponseList(List<Disease> entities);
}
