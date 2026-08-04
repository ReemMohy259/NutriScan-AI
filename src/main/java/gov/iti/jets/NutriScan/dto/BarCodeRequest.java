package gov.iti.jets.NutriScan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record BarCodeRequest(
    @NotBlank(message = "Barcode is required") @Pattern(regexp = "^(\\d{8}|\\d{12}|\\d{13}|\\d{14})$", message = "Barcode must be 8, 12, 13, or 14 digits") String barcode) {
}
