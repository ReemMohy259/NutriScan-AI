package gov.iti.jets.NutriScan.dto;

import gov.iti.jets.NutriScan.dto.ai.ScanStatus;

import java.util.UUID;

public record ScanNotification(UUID scanId, ScanStatus status) {
}