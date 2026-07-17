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
@Table(name = "user_diseases")
public class UserDisease {
    @EmbeddedId
    private UserDiseaseId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @MapsId("diseaseId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "disease_id", nullable = false)
    private Disease disease;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserDisease that = (UserDisease) o;
        return Objects.equals(user, that.user) && Objects.equals(disease, that.disease);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, disease);
    }
}