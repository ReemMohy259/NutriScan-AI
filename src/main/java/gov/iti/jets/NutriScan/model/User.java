package gov.iti.jets.NutriScan.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Size(max = 10)
    @Column(name = "gender", length = 20)
    private Gender gender;

    @Column(name = "height_cm", precision = 5, scale = 2)
    private BigDecimal heightCm;

    @Column(name = "weight_kg", precision = 5, scale = 2)
    private BigDecimal weightKg;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserAllergy> userAllergies = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserDisease> userDiseases = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    public void addAllergy(UserAllergy userAllergy){
        if(userAllergy != null){
            userAllergies.add(userAllergy);
            userAllergy.setUser(this);
        }
    }

    public void removeAllergy(UserAllergy userAllergy){
        if(userAllergy != null){
            userAllergies.remove(userAllergy);
            userAllergy.setUser(null);
        }
    }

    public void addDiseases(UserDisease userDisease){
        if(userDisease != null){
            userDiseases.add(userDisease);
            userDisease.setUser(this);
        }
    }

    public void removeDiseases(UserDisease userDisease){
        if(userDisease != null){
            userDiseases.remove(userDisease);
            userDisease.setUser(null);
        }
    }
}