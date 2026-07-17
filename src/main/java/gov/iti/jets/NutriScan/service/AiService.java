package gov.iti.jets.NutriScan.service;

import gov.iti.jets.NutriScan.dto.ai.FoodSafetyResponse;
import gov.iti.jets.NutriScan.dto.ai.OcrResponseDto;
import gov.iti.jets.NutriScan.exception.OcrModelException;
import jakarta.validation.constraints.NotNull;
import org.springframework.ai.content.Media;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;
import gov.iti.jets.NutriScan.dto.ai.IngredientsSafetyPrompt;
import gov.iti.jets.NutriScan.util.Prompts;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Objects;

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

    public OcrResponseDto checkImage(@NotNull MultipartFile image) {
        try {
            Media media = new Media(
                MediaType.parseMediaType(Objects.requireNonNull(image.getContentType())),
                new ByteArrayResource(image.getBytes()));

            return chatClient.prompt()
                .system(Prompts.OCR_SYSTEM)
                .user(
                    user -> user.text(
                        "analyze a single image and determine whether it represents a food product or information relevant to identifying a food product and its ingredients.")
                        .media(media))
                .call()
                .entity(OcrResponseDto.class);

        } catch (Exception e) {
            throw new OcrModelException("Failed to analyze image please try again later", e);
        }
    }
}