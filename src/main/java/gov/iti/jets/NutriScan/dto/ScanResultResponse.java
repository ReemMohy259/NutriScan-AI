package gov.iti.jets.NutriScan.dto;

import gov.iti.jets.NutriScan.dto.ai.FoodSafetyResponse;
import gov.iti.jets.NutriScan.dto.ai.ScanStatus;

import java.time.Instant;
import java.util.UUID;

public record ScanResultResponse(UUID scanId, ScanStatus status, Instant scannedAt, String imageUrl,
    FoodSafetyResponse foodSafetyResponse) {
}
