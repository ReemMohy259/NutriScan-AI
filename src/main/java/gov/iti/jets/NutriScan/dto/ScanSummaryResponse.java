package gov.iti.jets.NutriScan.dto;

import gov.iti.jets.NutriScan.dto.ai.Verdict;

import java.time.Instant;
import java.util.UUID;

public record ScanSummaryResponse(UUID scanId, String imageUrl, Verdict verdict,
    Instant scannedAt, String productName, Integer calories) {
}
