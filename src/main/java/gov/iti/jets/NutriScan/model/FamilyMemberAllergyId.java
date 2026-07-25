package gov.iti.jets.NutriScan.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FamilyMemberAllergyId implements Serializable {

    @Column(name = "family_member_id", nullable = false)
    private UUID familyMemberId;

    @Column(name = "allergy_id", nullable = false)
    private Integer allergyId;
}
