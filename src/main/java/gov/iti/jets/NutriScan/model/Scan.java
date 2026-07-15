package gov.iti.jets.NutriScan.model;

import gov.iti.jets.NutriScan.dto.Verdict;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "scans")
public class Scan {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "image_url", length = Integer.MAX_VALUE)
    private String imageUrl;

    @Size(max = 20)
    @NotNull
    @ColumnDefault("'PROCESSING'")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Size(max = 20)
    @Enumerated(EnumType.STRING)
    @Column(name = "verdict", length = 20)
    private Verdict verdict;

    @Column(name = "summary", length = Integer.MAX_VALUE)
    private String summary;

    @CreationTimestamp
    @Column(name = "scanned_at")
    private Instant scannedAt;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @OneToOne(mappedBy = "scans")
    private NutritionFact nutritionFact;

    @OneToMany
    private Set<ScanFlaggedIngredient> scanFlaggedIngredients = new LinkedHashSet<>();

}