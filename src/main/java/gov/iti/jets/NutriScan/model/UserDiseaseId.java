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
@ToString
@Builder
public class UserDiseaseId implements Serializable {
    private static final long serialVersionUID = -1425412187205125420L;
    @NotNull
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @NotNull
    @Column(name = "disease_id", nullable = false)
    private Integer diseaseId;

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof UserDiseaseId that))
            return false;

        return getUserId().equals(that.getUserId()) && getDiseaseId().equals(that.getDiseaseId());
    }

    @Override
    public int hashCode() {
        int result = getUserId().hashCode();
        result = 31 * result + getDiseaseId().hashCode();
        return result;
    }
}