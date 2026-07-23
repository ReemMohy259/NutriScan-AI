package gov.iti.jets.NutriScan.util.tools;

import org.springframework.ai.tool.annotation.Tool;

import java.util.concurrent.atomic.AtomicInteger;

public class LimitedTavilySearchTool {

    private final AtomicInteger calls = new AtomicInteger();
    private final TavilySearchTool delegate;

    public LimitedTavilySearchTool(TavilySearchTool delegate) {
        this.delegate = delegate;
    }

    @Tool(name = "web_search", description = """
        Search the internet for up-to-date information.
        Use this whenever search on spcifiec product ingredients
        recent research, recalls, news, or information after your
        training cutoff is required.
        """)
    public Object search(String query) {
        if (calls.incrementAndGet() > 2) {
            return "Search limit reached. Use the previous search results.";
        }

        return delegate.search(query);
    }
}
