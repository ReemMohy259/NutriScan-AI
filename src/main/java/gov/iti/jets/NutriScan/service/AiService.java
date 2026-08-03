package gov.iti.jets.NutriScan.service;

import com.fasterxml.jackson.databind.JsonNode;
import gov.iti.jets.NutriScan.ai.bedrock.BedrockGatewayStructuredChatClient;
import gov.iti.jets.NutriScan.ai.bedrock.StructuredChatClient;
import gov.iti.jets.NutriScan.ai.foodsafety.FoodSafetyJsonSchema;
import gov.iti.jets.NutriScan.dto.ai.*;
import gov.iti.jets.NutriScan.exception.MealModelException;
import gov.iti.jets.NutriScan.exception.OcrModelException;
import gov.iti.jets.NutriScan.util.Prompts;
import gov.iti.jets.NutriScan.util.tools.TavilySearchTool;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AiService {

    private final ChatClient chatClient;
    private final ChatClient opencodeChatClient;
    private final TavilySearchTool tavilySearchTool;
    private final JsonNode foodSafetySchema;
    private final StructuredChatClient structuredChatClient;

    public AiService(
        ChatClient chatClient,
        @Qualifier("openCodeChatClient") ChatClient opencodeChatClient,
        TavilySearchTool tavilySearchTool,
        ObjectMapper objectMapper,
        StructuredChatClient structuredChatClient) {
        this.chatClient = chatClient;
        this.opencodeChatClient = opencodeChatClient;
        this.tavilySearchTool = tavilySearchTool;

        this.foodSafetySchema = FoodSafetyJsonSchema.create(objectMapper);
        this.structuredChatClient = structuredChatClient;
    }

    public FoodSafetyResponse checkSafety(IngredientsSafetyPrompt requestData) {

        String userPrompt = """
            Analyze the ingredients and check if they are safe for consumption with the following context:
            ingredients: %s

            user allergies: %s

            user medical conditions: %s

            """
            .formatted(
                requestData.ingredients(),
                requestData.allergies(),
                requestData.conditions());

        // return structuredChatClient.generate(
        // Prompts.FOOD_SAFETY_SYSTEM,
        // userPrompt,
        // "food_safety_response",
        // foodSafetySchema,
        // 1000,
        // FoodSafetyResponse.class
        // );

        return opencodeChatClient.prompt()
            .system(Prompts.FOOD_SAFETY_SYSTEM)
            .user(userPrompt)
            .call()
            .entity(FoodSafetyResponse.class);
    }

    public FoodSafetyResponse checkBarcodeSafety(BarCodeSafetyPrompt requestData) {

        String userPrompt = """
            Analyze the product ingredients from OpenFoodFacts and check if they are safe
            for consumption with the following context:
            product: %s (barcode: %s)
            ingredients: %s
            user allergies: %s
            user medical conditions: %s
            """.formatted(
                requestData.productName(),
                requestData.barcode(),
                requestData.ingredients(),
                requestData.allergies(),
                requestData.conditions()
            );

        return opencodeChatClient.prompt()
            .system(Prompts.BARCODE_FOOD_SAFETY_SYSTEM)
            .user(userPrompt)
            .call()
            .entity(FoodSafetyResponse.class);
    }

    public MealFoodSafetyResponse mealCheckSafety(
        byte[] bytes,
        String contentType,
        MealIngredientsSafetyPrompt userData) {
        String userPrompt = """
            Analyze the meal and extract the ingredients and check if they are safe for consumption.

            User allergies:
            %s

            User medical conditions:
            %s
            """.formatted(userData.allergies(), userData.conditions());
        try {

            Media media = new Media(
                MediaType.parseMediaType(contentType),
                new ByteArrayResource(bytes));

            return chatClient.prompt()
                .system(Prompts.MEAL_FOOD_SAFETY_SYSTEM)
                .user(user -> user.text(userPrompt).media(media))
                .call()
                .entity(MealFoodSafetyResponse.class);

        } catch (Exception e) {
            e.printStackTrace();
            throw new MealModelException("Failed to analyze the meal please try again later");
        }
    }

    public OcrResponseDto checkImage(
        byte[] bytes,
        String contentType,
        @Nullable String originalFilename) {
        try {

            Media media = new Media(
                MediaType.parseMediaType(contentType),
                new ByteArrayResource(bytes));

            return chatClient.prompt()
                .system(Prompts.OCR_SYSTEM)
                .user(
                    user -> user.text(
                        "analyze a single image and determine whether it represents a food product or information relevant to identifying a food product and its ingredients.")
                        .media(media))
                .call()
                .entity(OcrResponseDto.class);

        } catch (Exception e) {
            e.printStackTrace();
            throw new OcrModelException("Failed to analyze image please try again later", e);
        }
    }

    public SearchModelResponseDto searchForIngredientsModel(String query, String productName) {

        String webResult = tavilySearchTool.search(query);

        String promptText = String.format(
            "get the ingredients of product: %s and the recommended search query is %s\n\n\n web result is: %s",
            productName,
            query,
            webResult);

        SearchModelResponseDto response = chatClient.prompt()
            .system(Prompts.SEARCH_MODEL_SYSTEM)
            .user(promptText)
            .call()
            .entity(SearchModelResponseDto.class);

        System.out.println("search Model Response: " + response);
        return response;
    }
}