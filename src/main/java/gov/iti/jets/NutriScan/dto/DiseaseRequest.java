package gov.iti.jets.NutriScan.dto;

import jakarta.validation.constraints.NotBlank;

public record DiseaseRequest(@NotBlank String name) {
}
