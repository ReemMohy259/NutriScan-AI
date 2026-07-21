package gov.iti.jets.NutriScan.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "diseases")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Disease {
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
        if (!(o instanceof Disease disease))
            return false;

        return Objects.equals(getName(), disease.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName());
    }
}