package gov.iti.jets.NutriScan.mapper;

import gov.iti.jets.NutriScan.dto.AllergyRequest;
import gov.iti.jets.NutriScan.dto.AllergyResponse;
import gov.iti.jets.NutriScan.model.Allergy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AllergyMapper {

    @Mapping(target = "id", ignore = true)
    Allergy toEntity(AllergyRequest request);

    AllergyResponse toResponse(Allergy entity);

    List<Allergy> toEntityList(List<AllergyRequest> requests);

    List<AllergyResponse> toResponseList(List<Allergy> entities);
}
