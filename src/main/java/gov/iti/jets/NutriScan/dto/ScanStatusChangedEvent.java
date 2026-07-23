package gov.iti.jets.NutriScan.dto;

import gov.iti.jets.NutriScan.dto.ai.ScanStatus;

import java.util.UUID;

public record ScanStatusChangedEvent(
        UUID userId,
        UUID scanId,
        ScanStatus status) {
}
