package gov.iti.jets.NutriScan.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcrResponseDto {

    @JsonProperty("is_food_product")
    private boolean foodProduct;

    @JsonProperty("is_relevant")
    private boolean relevant;

    @JsonProperty("need_search")
    private boolean needSearch;

    private List<String> ingredients;

    @JsonProperty("search_query")
    private String searchQuery;
}
