package gov.iti.jets.NutriScan.model;

import gov.iti.jets.NutriScan.dto.ai.ScanStatus;
import gov.iti.jets.NutriScan.dto.ai.Verdict;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Scan {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "image_url", length = Integer.MAX_VALUE)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'PROCESSING'")
    @Column(name = "status", nullable = false, length = 20)
    private ScanStatus status;

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

    @OneToOne(mappedBy = "scans", cascade = CascadeType.ALL, orphanRemoval = true)
    private NutritionFact nutritionFact;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "scan_id")
    private Set<ScanFlaggedIngredient> scanFlaggedIngredients = new LinkedHashSet<>();

    @Column(nullable = false)
    @Builder.Default
    private Boolean favorite = false;

    public void addFlaggedIngredient(ScanFlaggedIngredient scanFlaggedIngredient) {
        if (scanFlaggedIngredient != null) {
            scanFlaggedIngredients.add(scanFlaggedIngredient);
            scanFlaggedIngredient.setScan(this);
        }
    }

    public void removeFlaggedIngredient(ScanFlaggedIngredient scanFlaggedIngredient) {
        if (scanFlaggedIngredient != null) {
            scanFlaggedIngredients.remove(scanFlaggedIngredient);
            scanFlaggedIngredient.setScan(null);
        }
    }

}