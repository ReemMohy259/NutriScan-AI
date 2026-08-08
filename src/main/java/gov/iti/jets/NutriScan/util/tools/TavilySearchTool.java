package gov.iti.jets.NutriScan.util.tools;

import gov.iti.jets.NutriScan.dto.ai.TavilyRequest;
import gov.iti.jets.NutriScan.dto.ai.TavilyResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
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
            List.of(
                "metro-markets.com",
                "gourmetegypt.com",
                "seoudi.com",
                "rabbitmart.com",
                "breadfast.com",
                "carrefouregypt.com",
                "jumia.com.eg",
                "elhussien.com",
                "amazon.eg",
                "talabat.com",
                "juhayna.com",
                "faragalla.com",
                "edita.com.eg",
                "domty.org",
                "sakrgroup.net",
                "rehanaproducts.com",
                "arabunion-eg.com",
                "kagegypt.com",
                "migfood.com",
                "bcfegypt.com",
                "egygulf-foods.com"),
            List.of(),
            "egypt");

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

            sb.append("URL: ").append(r.url()).append("\n");

            sb.append("Content:\n").append(r.content()).append("\n");

            try {
                if (!r.content().toLowerCase().contains("ingredient")
                    && !r.content().toLowerCase().contains("ingredients")) {
                    Document document = Jsoup.connect(r.url())
                        .userAgent("Mozilla/5.0")
                        .timeout(7000)
                        .get();

                    String cleanText = document.body()
                        .text()
                        .replaceAll("[\\p{C}]", "\n")
                        .replaceAll("\\s+", " ")
                        .trim();

                    sb.append("Raw content: ").append(cleanText).append("\n");
                    System.out.println(cleanText);
                }
            } catch (IOException e) {
            }

        }

        System.out.println("tool result: " + sb);
        return sb.toString();
    }

}
