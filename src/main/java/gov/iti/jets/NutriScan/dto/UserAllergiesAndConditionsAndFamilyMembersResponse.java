package gov.iti.jets.NutriScan.dto;

import gov.iti.jets.NutriScan.dto.ai.FamilyMemberAiRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAllergiesAndConditionsAndFamilyMembersResponse {
    private List<String> allergies;
    private List<String> diseases;
    private List<FamilyMemberAiRequest> familyMembers;
}
