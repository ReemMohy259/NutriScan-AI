package gov.iti.jets.NutriScan.dto;

import jakarta.validation.constraints.NotBlank;

public record AllergyRequest(@NotBlank String name) {
}
