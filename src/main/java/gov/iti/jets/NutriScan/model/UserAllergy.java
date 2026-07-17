package gov.iti.jets.NutriScan.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "user_allergies")
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
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserAllergy that = (UserAllergy) o;
        return Objects.equals(user, that.user) && Objects.equals(allergy, that.allergy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, allergy);
    }
}