package gov.iti.jets.NutriScan.dto;

import gov.iti.jets.NutriScan.model.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RegisterRequest(

    @NotBlank @Pattern(regexp = ".*\\p{L}.*", message = "First name must contain at least one letter") @Size(max = 100, min = 3) String firstName,

    @NotBlank @Pattern(regexp = ".*\\p{L}.*", message = "Last name must contain at least one letter") @Size(max = 100, min = 3) String lastName,

    @NotBlank @Email @Size(max = 255) String email,

    @NotBlank @Size(max = 100, min = 3) String username,

    @NotBlank @Size(min = 8, max = 128) String password,

    @Past LocalDate dateOfBirth,

    Gender gender,

    @DecimalMin(value = "0.0", inclusive = false) BigDecimal heightCm,

    @DecimalMin(value = "0.0", inclusive = false) BigDecimal weightKg,

    List<@Valid @NotNull FamilyMemberRequest> familyMembers,

    List<@NotNull Integer> allergies,

    List<@NotNull Integer> diseases) {
}
