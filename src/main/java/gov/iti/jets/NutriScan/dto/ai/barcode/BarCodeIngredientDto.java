package gov.iti.jets.NutriScan.dto.ai.barcode;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class BarCodeIngredientDto {
    private String id;
    private String text;
    private String vegan;
    private String vegetarian;

    @JsonProperty("percent_estimate")
    private Double percentEstimate;

}
