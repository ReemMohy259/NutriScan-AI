package gov.iti.jets.NutriScan.dto;

import java.util.List;
import java.util.UUID;

public record ScanSearchResult(
        List<UUID> ids,
        long totalElements
) {
}