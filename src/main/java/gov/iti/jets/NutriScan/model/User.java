package gov.iti.jets.NutriScan.model;

import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import lombok.*;
import org.hibernate.annotations.*;

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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "daily_streak", nullable = false)
    @Builder.Default
    private Integer dailyStreak = 0;

    @Column(name = "last_active_date")
    private LocalDate lastActiveDate;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 5)
    private Set<FamilyMember> familyMembers = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<DailyTracking> dailyTrackings = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Scan> scans = new HashSet<>();

    @Column(name = "image_url")
    private String imageUrl;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status")
    @Builder.Default
    private AccountStatus accountStatus = AccountStatus.ACTIVE;

    @Column(name = "to_be_deleted_at")
    private LocalDate toBeDeletedAt;

    @Column(name = "deletion_requested_at")
    private Instant deletionRequestedAt;

    public void addAllergy(UserAllergy userAllergy) {
        if (userAllergy != null) {
            userAllergies.add(userAllergy);
            userAllergy.setUser(this);
        }
    }

    public void removeAllergy(UserAllergy userAllergy) {
        if (userAllergy != null) {
            userAllergies.remove(userAllergy);
        }
    }

    public void addDiseases(UserDisease userDisease) {
        if (userDisease != null) {
            userDiseases.add(userDisease);
            userDisease.setUser(this);
        }
    }

    public void removeDiseases(UserDisease userDisease) {
        if (userDisease != null) {
            userDiseases.remove(userDisease);
        }
    }

    public void addFamilyMember(FamilyMember member) {
        if (member != null) {
            familyMembers.add(member);
            member.setUser(this);
        }
    }

    public void removeFamilyMember(FamilyMember member) {
        if (member != null) {
            familyMembers.remove(member);
        }
    }
}