package gov.iti.jets.NutriScan.mapper;

import gov.iti.jets.NutriScan.dto.AllergyResponse;
import gov.iti.jets.NutriScan.dto.DiseaseResponse;
import gov.iti.jets.NutriScan.dto.FamilyMemberResponse;
import gov.iti.jets.NutriScan.dto.ai.FamilyMemberAiRequest;
import gov.iti.jets.NutriScan.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {AllergyMapper.class, DiseaseMapper.class})
public interface FamilyMemberMapper {

    @Mapping(target = "allergies", source = "allergies")
    @Mapping(target = "diseases", source = "diseases")
    FamilyMemberResponse toResponse(FamilyMember entity);

    List<FamilyMemberResponse> toResponseList(List<FamilyMember> entities);

    List<FamilyMemberAiRequest> toAiResponseList(List<FamilyMember> entities);

    default List<AllergyResponse> mapAllergies(Set<FamilyMemberAllergy> allergies) {
        return allergies.stream().map(FamilyMemberAllergy::getAllergy).map(this::map).toList();
    }

    default List<DiseaseResponse> mapDiseases(Set<FamilyMemberDisease> diseases) {
        return diseases.stream().map(FamilyMemberDisease::getDisease).map(this::map).toList();
    }

    AllergyResponse map(Allergy allergy);

    DiseaseResponse map(Disease disease);
}