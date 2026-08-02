package gov.iti.jets.NutriScan.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "family_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FamilyMember {

    @Id
    private UUID id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 150)
    private String relation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "familyMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FamilyMemberAllergy> allergies = new HashSet<>();

    @OneToMany(mappedBy = "familyMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FamilyMemberDisease> diseases = new HashSet<>();
}