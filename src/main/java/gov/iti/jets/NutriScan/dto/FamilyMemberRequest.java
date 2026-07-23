package gov.iti.jets.NutriScan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record FamilyMemberRequest(

    @NotBlank @Size(max = 150) String name, List<@NotNull Integer> allergyIds,
    List<@NotNull Integer> diseaseIds) {
}