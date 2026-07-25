package gov.iti.jets.NutriScan.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FamilyMemberDiseaseId implements Serializable {

    @Column(name = "family_member_id", nullable = false)
    private UUID familyMemberId;

    @Column(name = "disease_id", nullable = false)
    private Integer diseaseId;
}
