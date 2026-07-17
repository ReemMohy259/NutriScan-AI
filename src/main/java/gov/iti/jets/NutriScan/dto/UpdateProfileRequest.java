package gov.iti.jets.NutriScan.dto;

import gov.iti.jets.NutriScan.model.Gender;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record UpdateProfileRequest(@Size(max = 100) String firstName,

    @Size(max = 100) String lastName,

    @Past LocalDate dateOfBirth,

    Gender gender,

    @DecimalMin(value = "0.0", inclusive = false) BigDecimal heightCm,

    @DecimalMin(value = "0.0", inclusive = false) BigDecimal weightKg,

    List<@NotBlank Integer> allergies,

    List<@NotBlank Integer> diseases) {
}
