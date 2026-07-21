package gov.iti.jets.NutriScan.dto;

import gov.iti.jets.NutriScan.model.Gender;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RegisterRequest(@NotBlank @Size(max = 100) String firstName,

    @NotBlank @Size(max = 100) String lastName,

    @NotBlank @Email @Size(max = 255) String email,

    @NotBlank @Size(max = 100) String username,

    @NotBlank @Size(min = 8, max = 128) String password,

    @Past LocalDate dateOfBirth,

    Gender gender,

    @DecimalMin(value = "0.0", inclusive = false) BigDecimal heightCm,

    @DecimalMin(value = "0.0", inclusive = false) BigDecimal weightKg,

    List<@NotNull Integer> allergies,

    List<@NotNull Integer> diseases) {
}
