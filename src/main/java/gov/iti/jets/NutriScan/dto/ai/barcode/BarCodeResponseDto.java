package gov.iti.jets.NutriScan.dto.ai.barcode;

import lombok.Data;

@Data
public class BarCodeResponseDto {

    private Integer status;

    private BarCodeProductDto product;
}
