package gov.iti.jets.NutriScan.dto;

import java.util.List;
import java.util.UUID;

public record FamilyMemberResponse(UUID id, String name, String relation,
    List<AllergyResponse> allergies, List<DiseaseResponse> diseases) {
}