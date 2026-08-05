package gov.iti.jets.NutriScan.config;

import com.google.genai.Client;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.retry.RetryTemplate;

@Configuration
public class AiConfig {

    @Value("${gemini.api.key.search}")
    private String apiKeySearch;
    @Value("${gemini.api.key.ocr}")
    private String apiKeyOcr;
    @Value("${gemini.api.key.judge}")
    private String apiKeyJudge;

    @Value("${gemini.model.name}")
    private String modelName;

    private GoogleGenAiChatOptions getChatOptions() {
        return GoogleGenAiChatOptions.builder()
                .model(modelName)
                .build();
    }

    @Bean
    public ChatClient geminiSearch(
            ToolCallingManager toolCallingManager,
            ObjectProvider<RetryTemplate> retryProvider,
            ObjectProvider<ObservationRegistry> observationProvider) {

        Client genAiClient = Client.builder().apiKey(apiKeySearch).build();

        GoogleGenAiChatModel chatModel = new GoogleGenAiChatModel(
                genAiClient,
                getChatOptions(),
                toolCallingManager,
                retryProvider.getIfAvailable(RetryTemplate::new),
                observationProvider.getIfAvailable(() -> ObservationRegistry.NOOP)
        );

        return ChatClient.builder(chatModel).build();
    }

    @Bean
    public ChatClient geminiOcr(
            ToolCallingManager toolCallingManager,
            ObjectProvider<RetryTemplate> retryProvider,
            ObjectProvider<ObservationRegistry> observationProvider) {

        Client genAiClient = Client.builder().apiKey(apiKeyOcr).build();

        GoogleGenAiChatModel chatModel = new GoogleGenAiChatModel(
                genAiClient,
                getChatOptions(),
                toolCallingManager,
                retryProvider.getIfAvailable(RetryTemplate::new),
                observationProvider.getIfAvailable(() -> ObservationRegistry.NOOP)
        );

        return ChatClient.builder(chatModel).build();
    }
    @Bean
    public ChatClient geminiJudge(
            ToolCallingManager toolCallingManager,
            ObjectProvider<RetryTemplate> retryProvider,
            ObjectProvider<ObservationRegistry> observationProvider) {

        Client genAiClient = Client.builder().apiKey(apiKeyJudge).build();

        GoogleGenAiChatModel chatModel = new GoogleGenAiChatModel(
                genAiClient,
                getChatOptions(),
                toolCallingManager,
                retryProvider.getIfAvailable(RetryTemplate::new),
                observationProvider.getIfAvailable(() -> ObservationRegistry.NOOP)
        );

        return ChatClient.builder(chatModel).build();
    }

//    @Bean
//    ChatClient geminiChatClient(GoogleGenAiChatModel chatModel) {
//        return ChatClient.builder(chatModel).build();
//    }

    @Bean
    ChatClient openCodeChatClient(OpenAiChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }

}
