package gov.iti.jets.NutriScan.dto;

import java.time.LocalDate;

public record DeleteAccountResponse(LocalDate scheduledDeletionAt, int gracePeriodDays) {
}