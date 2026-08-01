package gov.iti.jets.NutriScan.dto;

import java.time.Instant;

public record RestoreAccountResponse(
        String message,
        Instant restoredAt
) {
}