package gov.iti.jets.NutriScan.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.iti.jets.NutriScan.ai.bedrock.BedrockGatewayStructuredChatClient;
import gov.iti.jets.NutriScan.ai.bedrock.StructuredChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BedrockConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public StructuredChatClient structuredChatClient(
        ObjectMapper objectMapper,
        @Value("${bedrock.base-url}") String baseUrl,
        @Value("${bedrock.api-key}") String apiKey,
        @Value("${bedrock.model-id:deepseek.v3.2}") String modelId
    ) {
        return new BedrockGatewayStructuredChatClient(
            baseUrl,
            apiKey,
            modelId,
            objectMapper
        );
    }
}
