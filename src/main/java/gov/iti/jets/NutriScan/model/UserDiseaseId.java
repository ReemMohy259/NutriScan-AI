package gov.iti.jets.NutriScan.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDiseaseId implements Serializable {
    private static final long serialVersionUID = -1425412187205125420L;
    @NotNull
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @NotNull
    @Column(name = "disease_id", nullable = false)
    private Integer diseaseId;

}