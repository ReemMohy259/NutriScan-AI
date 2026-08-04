package gov.iti.jets.NutriScan.ai.bedrock;

import com.fasterxml.jackson.databind.JsonNode;

public interface StructuredChatClient {

    <T> T generate(
        String systemPrompt,
        String userPrompt,
        String schemaName,
        JsonNode jsonSchema,
        int maxTokens,
        Class<T> responseType);
}
