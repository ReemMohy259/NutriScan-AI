package gov.iti.jets.NutriScan.dto;

import gov.iti.jets.NutriScan.model.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record UpdateProfileRequest(
    @Size(max = 100) @Pattern(regexp = ".*\\p{L}.*", message = "First name must contain at least one letter") String firstName,

    @Size(max = 100) @Pattern(regexp = ".*\\p{L}.*", message = "Last name must contain at least one letter") String lastName,

    @Past LocalDate dateOfBirth,

    Gender gender,

    @DecimalMin(value = "0.0", inclusive = false) @DecimalMax(value = "300.0") BigDecimal heightCm,

    @DecimalMin(value = "0.0", inclusive = false) @DecimalMax(value = "600.0") BigDecimal weightKg,

    List<@NotNull Integer> allergyIds,

    List<@NotNull Integer> diseaseIds,

    List<@Valid @NotNull FamilyMemberUpdateRequest> familyMembers) {
}
