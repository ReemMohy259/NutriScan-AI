package gov.iti.jets.NutriScan.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "family_member_allergies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FamilyMemberAllergy {

    @EmbeddedId
    private FamilyMemberAllergyId id;

    @MapsId("familyMemberId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_member_id")
    private FamilyMember familyMember;

    @MapsId("allergyId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allergy_id")
    private Allergy allergy;
}