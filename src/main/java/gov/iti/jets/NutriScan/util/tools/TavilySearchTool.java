package gov.iti.jets.NutriScan.util.tools;

import gov.iti.jets.NutriScan.dto.ai.TavilyRequest;
import gov.iti.jets.NutriScan.dto.ai.TavilyResponse;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class TavilySearchTool {

    private final RestClient client;

    @Value("${tavily.api-key}")
    private String apiKey;

    @Value("${tavily.url}")
    private String url;

    public TavilySearchTool(RestClient client) {
        this.client = client;
    }

    @Tool(name = "web_search", description = """
        Search the internet for up-to-date information.
        Use this whenever search on spcifiec product ingredients
        recent research, recalls, news, or information after your
        training cutoff is required.
        """)
    public String search(String query) {

        TavilyRequest request = new TavilyRequest(
            apiKey,
            query,
            3,
            true,
            false,
            false,
            List.of(),
            List.of());

        TavilyResponse response = client.post()
            .uri(url)
            .body(request)
            .retrieve()
            .body(TavilyResponse.class);

        if (response == null) {
            return "No search results.";
        }

        StringBuilder sb = new StringBuilder();

        if (response.answer() != null) {
            sb.append("Summary:\n").append(response.answer()).append("\n\n");
        }

        for (TavilyResponse.SearchResult r : response.results()) {

            sb.append("Title: ").append(r.title()).append("\n");

            sb.append("Content: ").append(r.content()).append("\n");

            sb.append("Source: ").append(r.url()).append("\n\n");
        }

        System.out.println("tool result: " + sb.toString());
        return sb.toString();
    }

}
