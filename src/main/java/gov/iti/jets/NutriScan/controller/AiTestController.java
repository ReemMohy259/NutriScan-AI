package gov.iti.jets.NutriScan.controller;

import gov.iti.jets.NutriScan.dto.ai.FoodSafetyResponse;
import gov.iti.jets.NutriScan.dto.ai.IngredientsSafetyPrompt;
import gov.iti.jets.NutriScan.service.AiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AiTestController {
    private final AiService aiService;

    public AiTestController(AiService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/test")
    public FoodSafetyResponse test() {
        return aiService.checkSafety(
            new IngredientsSafetyPrompt(
                List.of("Whole Wheat Flour", "Water", "Yeast", "Salt"),
                List.of(),
                List.of("Celiac disease", "Gluten sensitivity")));
    }
}
