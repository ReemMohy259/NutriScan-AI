package gov.iti.jets.NutriScan.service;

import gov.iti.jets.NutriScan.dto.ai.barcode.BarCodeProductDto;
import gov.iti.jets.NutriScan.dto.ai.barcode.BarCodeResponseDto;
import gov.iti.jets.NutriScan.exception.BarCodeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OpenFoodFactsService {

    private final RestTemplate restTemplate;

    private static final String URL = "https://world.openfoodfacts.org/api/v2/product/%s.json";

    public BarCodeProductDto getProduct(String barcode) {

        try {
            BarCodeResponseDto response = restTemplate
                .getForObject(String.format(URL, barcode), BarCodeResponseDto.class);

            if (response == null || response.getStatus() == 0 || response.getProduct() == null) {
                throw new BarCodeNotFoundException("No product found for barcode: " + barcode);
            }

            return response.getProduct();

        } catch (HttpClientErrorException.NotFound ex) {
            throw new BarCodeNotFoundException("No product found for barcode: " + barcode);
        }
    }
}
