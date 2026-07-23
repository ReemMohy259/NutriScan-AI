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
public class UserAllergyId implements Serializable {
    private static final long serialVersionUID = -3124391931087733944L;
    @NotNull
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @NotNull
    @Column(name = "allergy_id", nullable = false)
    private Integer allergyId;

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof UserAllergyId that))
            return false;

        return getUserId().equals(that.getUserId()) && getAllergyId().equals(that.getAllergyId());
    }

    @Override
    public int hashCode() {
        int result = getUserId().hashCode();
        result = 31 * result + getAllergyId().hashCode();
        return result;
    }
}