package gov.iti.jets.NutriScan.listener.event;

import java.util.UUID;

public record ScanUpdatedEvent(
        UUID scanId
) {
}