package gov.iti.jets.NutriScan.dto;

import gov.iti.jets.NutriScan.model.Gender;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CurrentUserProfileResponse(
        UUID id,

        String email,

        String username,

        String firstName,

        String lastName,

        LocalDate dateOfBirth,

        Gender gender,

        BigDecimal heightCm,

        BigDecimal weightKg,

        List<AllergyResponse> allergies,

        List<DiseaseResponse> diseases,

        Instant updatedAt){
}
