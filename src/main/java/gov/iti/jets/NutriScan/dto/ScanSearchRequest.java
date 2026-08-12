package gov.iti.jets.NutriScan.dto;

import gov.iti.jets.NutriScan.dto.ai.ScanStatus;
import gov.iti.jets.NutriScan.dto.ai.Verdict;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

public record ScanSearchRequest(String query, Verdict verdict, ScanStatus scanStatus,
    LocalDate date, int page, int size) {

    public boolean hasFilters() {
        return StringUtils.hasText(query) || verdict != null || scanStatus != null || date != null;
    }
}