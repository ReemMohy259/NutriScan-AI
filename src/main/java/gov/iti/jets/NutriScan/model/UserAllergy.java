package gov.iti.jets.NutriScan.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "user_allergies")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAllergy {
    @EmbeddedId
    private UserAllergyId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @MapsId("allergyId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "allergy_id", nullable = false)
    private Allergy allergy;

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof UserAllergy that))
            return false;

        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}