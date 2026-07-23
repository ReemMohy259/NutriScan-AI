package gov.iti.jets.NutriScan.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "user_diseases")
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    public final boolean equals(Object o) {
        if (!(o instanceof UserDisease that))
            return false;

        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}