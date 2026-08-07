package gov.iti.jets.NutriScan.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcrResponseDto {

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("is_food_product")
    private boolean foodProduct;

    @JsonProperty("is_relevant")
    private boolean relevant;

    @JsonProperty("need_search")
    private boolean needSearch;

    private List<String> ingredients;

    @JsonProperty("search_query")
    private String searchQuery;

    @JsonProperty("is_meal")
    private boolean isMeal;

    @JsonProperty("nutrition_facts")
    private NutritionFactsDto nutritionFacts;

    @JsonProperty("is_blurry")
    private boolean isBlurry;
}
