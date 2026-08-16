package gov.iti.jets.NutriScan.dto.ai;

import gov.iti.jets.NutriScan.dto.AllergyResponse;
import gov.iti.jets.NutriScan.dto.DiseaseResponse;

import java.util.List;

public record FamilyMemberAiRequest(String name, List<AllergyResponse> allergies,
    List<DiseaseResponse> diseases) {
}
