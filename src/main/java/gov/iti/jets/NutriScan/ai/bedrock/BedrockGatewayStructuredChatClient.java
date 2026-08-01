package gov.iti.jets.NutriScan.ai.bedrock;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.iti.jets.NutriScan.exception.BedrockGatewayException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public final class BedrockGatewayStructuredChatClient
    implements StructuredChatClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final URI chatEndpoint;
    private final String apiKey;
    private final String modelId;

    public BedrockGatewayStructuredChatClient(
        String baseUrl,
        String apiKey,
        String modelId,
        ObjectMapper objectMapper
    ) {
        Objects.requireNonNull(baseUrl, "baseUrl is required");
        Objects.requireNonNull(apiKey, "apiKey is required");
        Objects.requireNonNull(modelId, "modelId is required");

        this.objectMapper = Objects.requireNonNull(
            objectMapper,
            "objectMapper is required"
        );

        if (baseUrl.isBlank()) {
            throw new IllegalArgumentException("baseUrl cannot be blank");
        }

        if (apiKey.isBlank()) {
            throw new IllegalArgumentException("apiKey cannot be blank");
        }

        if (modelId.isBlank()) {
            throw new IllegalArgumentException("modelId cannot be blank");
        }

        String normalizedBaseUrl = baseUrl.endsWith("/")
            ? baseUrl.substring(0, baseUrl.length() - 1)
            : baseUrl;

        this.chatEndpoint = URI.create(
            normalizedBaseUrl + "/student/chat"
        );

        this.apiKey = apiKey;
        this.modelId = modelId;

        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(20))
                .build();
    }

    @Override
    public <T> T generate(
        String systemPrompt,
        String userPrompt,
        String schemaName,
        JsonNode jsonSchema,
        int maxTokens,
        Class<T> responseType
    ) {
        Objects.requireNonNull(systemPrompt, "systemPrompt is required");
        Objects.requireNonNull(userPrompt, "userPrompt is required");
        Objects.requireNonNull(schemaName, "schemaName is required");
        Objects.requireNonNull(jsonSchema, "jsonSchema is required");
        Objects.requireNonNull(responseType, "responseType is required");

        if (maxTokens <= 0) {
            throw new IllegalArgumentException(
                "maxTokens must be greater than zero"
            );
        }

        GatewayChatRequest requestBody = new GatewayChatRequest(
            modelId,
            List.of(new GatewayMessage("user", userPrompt)),
            systemPrompt,
            maxTokens,
            new ResponseFormat(
                "json_schema",
                new JsonSchemaDefinition(
                    schemaName,
                    true,
                    jsonSchema
                )
            )
        );

        try {
            String requestJson =
                objectMapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(chatEndpoint)
                .timeout(Duration.ofSeconds(90))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

            HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() >= 400) {
                throw new BedrockGatewayException(
                    "Bedrock gateway returned HTTP "
                        + response.statusCode()
                        + ": "
                        + response.body()
                );
            }

            String structuredJson =
                extractStructuredJson(response.body());

            return objectMapper.readValue(
                structuredJson,
                    responseType
            );

        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            throw new BedrockGatewayException(
                "Bedrock gateway request was interrupted",
                exception
            );

        } catch (IOException exception) {
            exception.printStackTrace();
            throw new BedrockGatewayException(
                "Failed to communicate with the Bedrock gateway",
                exception
            );
        }
    }

    private String extractStructuredJson(String responseBody)
        throws JsonProcessingException {

        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode contentNode = findContentNode(root);

        if (contentNode == null
            || contentNode.isMissingNode()
            || contentNode.isNull()) {

            return responseBody;
        }

        if (contentNode.isObject()) {
            return contentNode.toString();
        }

        if (contentNode.isTextual()) {
            return removeMarkdownCodeFence(contentNode.asText());
        }

        if (contentNode.isArray()) {
            StringBuilder result = new StringBuilder();

            for (JsonNode item : contentNode) {
                if (item.isTextual()) {
                    result.append(item.asText());
                } else if (item.has("text")) {
                    result.append(item.get("text").asText());
                }
            }

            return removeMarkdownCodeFence(result.toString());
        }

        return contentNode.toString();
    }

    private JsonNode findContentNode(JsonNode root) {
        JsonNode node = root.at("/choices/0/message/content");

        if (!node.isMissingNode()) {
            return node;
        }

        node = root.at("/message/content");

        if (!node.isMissingNode()) {
            return node;
        }

        if (root.has("content")) {
            return root.get("content");
        }

        if (root.has("output_text")) {
            return root.get("output_text");
        }

        if (root.has("response")) {
            return root.get("response");
        }

        return null;
    }

    private String removeMarkdownCodeFence(String value) {
        String cleaned = value.trim();

        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }

        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(
                0,
                cleaned.length() - 3
            );
        }

        return cleaned.trim();
    }

    private record GatewayChatRequest(
        @JsonProperty("model_id")
        String modelId,

        List<GatewayMessage> messages,

        @JsonProperty("system_prompt")
        String systemPrompt,

        @JsonProperty("max_tokens")
        int maxTokens,

        @JsonProperty("response_format")
        ResponseFormat responseFormat
    ) {
    }

    private record GatewayMessage(
        String role,
        String content
    ) {
    }

    private record ResponseFormat(
        String type,

        @JsonProperty("json_schema")
        JsonSchemaDefinition jsonSchema
    ) {
    }

    private record JsonSchemaDefinition(
        String name,
        boolean strict,
        JsonNode schema
    ) {
    }
}
