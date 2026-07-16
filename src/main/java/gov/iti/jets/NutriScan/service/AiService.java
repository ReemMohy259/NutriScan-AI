package gov.iti.jets.NutriScan.service;

import gov.iti.jets.NutriScan.dto.ai.FoodSafetyResponse;
import tools.jackson.databind.ObjectMapper;
import gov.iti.jets.NutriScan.dto.ai.IngredientsSafetyPrompt;
import gov.iti.jets.NutriScan.util.Prompts;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public AiService(ChatClient chatClient, ObjectMapper objectMapper) {
        this.chatClient = chatClient;
        this.objectMapper = objectMapper;
    }
    public FoodSafetyResponse checkSafety(IngredientsSafetyPrompt requestData) {
        String userPrompt = objectMapper.writeValueAsString(requestData);

        return chatClient.prompt()
            .system(Prompts.FOOD_SAFETY_SYSTEM)
            .user(userPrompt)
            .call()
            .entity(FoodSafetyResponse.class);
    }
}