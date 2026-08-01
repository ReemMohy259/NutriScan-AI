package gov.iti.jets.NutriScan.dto.ai;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TavilyResponse(String answer, List<SearchResult> results) {

    public record SearchResult(String title, String url, String content, double score,
        @JsonProperty("raw_content") String rawContent) {
    }
}
