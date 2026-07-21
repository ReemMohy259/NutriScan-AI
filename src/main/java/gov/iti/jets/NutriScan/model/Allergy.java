package gov.iti.jets.NutriScan.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "allergies")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Allergy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 150)
    @NotNull
    @Column(name = "name", nullable = false, length = 150, unique = true)
    private String name;

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Allergy allergy))
            return false;

        return Objects.equals(getName(), allergy.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName());
    }
}