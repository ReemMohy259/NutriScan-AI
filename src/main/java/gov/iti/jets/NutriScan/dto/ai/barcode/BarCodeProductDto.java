package gov.iti.jets.NutriScan.dto.ai.barcode;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class BarCodeProductDto {

    @JsonProperty("product_name")
    private String productName;

    private String brands;

    @JsonProperty("product_type")
    private String productType;

    @JsonProperty("categories_tags")
    private List<String> categoriesTags;

    @JsonProperty("food_groups")
    private String foodGroups;

    @JsonProperty("ingredients_text")
    private String ingredientsText;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("ingredients_tags")
    private List<String> ingredientsTags;

    private List<BarCodeIngredientDto> ingredients;

    @JsonProperty("allergens_tags")
    private List<String> allergensTags;

    @JsonProperty("traces_tags")
    private List<String> tracesTags;

    private BarCodeNutrimentsDto nutriments;

    @JsonProperty("nova_group")
    private Integer novaGroup;
}