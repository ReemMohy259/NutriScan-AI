package gov.iti.jets.NutriScan.dto;

import java.time.Instant;

public record DeleteAccountResponse(Instant scheduledDeletionAt, int gracePeriodDays) {
}