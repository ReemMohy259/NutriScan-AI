package gov.iti.jets.NutriScan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAllergiesAndConditionsResponse {
    private List<String> allergies;
    private List<String> diseases;
}
