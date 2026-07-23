package gov.iti.jets.NutriScan.service;

import gov.iti.jets.NutriScan.dto.ai.*;
import gov.iti.jets.NutriScan.exception.MealModelException;
import gov.iti.jets.NutriScan.exception.OcrModelException;
import gov.iti.jets.NutriScan.util.Prompts;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
public class AiService {

    private final ChatClient chatClient;
    private final ChatClient opencodeChatClient;

    public AiService(
        ChatClient chatClient,
        @Qualifier("openCodeChatClient") ChatClient opencodeChatClient,
        ObjectMapper objectMapper) {
        this.chatClient = chatClient;
        this.opencodeChatClient = opencodeChatClient;
    }

    public FoodSafetyResponse checkSafety(IngredientsSafetyPrompt requestData) {
        // TODO: add nutrition facts to the prompt
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

        return opencodeChatClient.prompt()
            .system(Prompts.FOOD_SAFETY_SYSTEM)
            .user(userPrompt)
            .call()
            .entity(FoodSafetyResponse.class);
    }

    public MealFoodSafetyResponse mealCheckSafety(
        byte[] bytes,
        String contentType,
        MealIngredientsSafetyPrompt userData) {
        // TODO: add nutrition facts to the prompt
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

    // public List<String> searchModelGemini(String query, String productName) {
    // String promptText = String.format("get the ingredients of product: %s and the
    // recommended search query is %s", productName, query);
    //
    // List<String> response = chatClient.prompt()
    // .system(Prompts.SEARCH_MODEL_SYSTEM)
    // .user(promptText)
    // .call()
    // .entity(new ParameterizedTypeReference<List<String>>() {});

    // System.out.println("Gemini Response: " + response);
    // return response;
    // }
}