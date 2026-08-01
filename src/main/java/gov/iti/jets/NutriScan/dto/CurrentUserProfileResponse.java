package gov.iti.jets.NutriScan.dto;

import gov.iti.jets.NutriScan.model.Gender;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
public record CurrentUserProfileResponse(UUID id,

    String email,

    String username,

    String firstName,

    String lastName,

    Integer dailyStreak,

    String imageUrl,

    LocalDate dateOfBirth,

    Gender gender,

    BigDecimal heightCm,

    BigDecimal weightKg,

    Double bmi,

    Double tdee,

    List<AllergyResponse> allergies,

    List<DiseaseResponse> diseases,

    List<FamilyMemberResponse> familyMembers,

    Instant updatedAt) {
}
