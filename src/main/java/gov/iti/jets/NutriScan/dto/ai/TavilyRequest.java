package gov.iti.jets.NutriScan.dto.ai;

import java.util.List;

public record TavilyRequest(String api_key, String query, int max_results, boolean include_answer,
    boolean include_raw_content, boolean include_images, List<String> include_domains,
    List<String> exclude_domains) {
}
