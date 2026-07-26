package gov.iti.jets.NutriScan.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateScanDto(
    @Pattern(regexp = ".*\\S.*", message = "Scan name must not be blank") @Size(max = 60, message = "Scan name must not exceed 60 characters") String name,
    Boolean favorite) {
}
