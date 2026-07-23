package gov.iti.jets.NutriScan.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "family_member_diseases")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FamilyMemberDisease {

    @EmbeddedId
    private FamilyMemberDiseaseId id;

    @MapsId("familyMemberId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_member_id")
    private FamilyMember familyMember;

    @MapsId("diseaseId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disease_id")
    private Disease disease;
}