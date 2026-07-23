package gov.iti.jets.NutriScan.dto.ai;

import java.util.List;

public record TavilyResponse(String answer, List<SearchResult> results) {

    public record SearchResult(String title, String url, String content, double score) {
    }
}
