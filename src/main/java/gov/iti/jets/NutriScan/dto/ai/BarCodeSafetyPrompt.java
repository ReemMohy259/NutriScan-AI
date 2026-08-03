package gov.iti.jets.NutriScan.dto.ai;

import java.util.List;

public record BarCodeSafetyPrompt(String barcode, String productName, List<String> categories,
    List<String> ingredients, List<String> allergies, List<String> conditions) {
}