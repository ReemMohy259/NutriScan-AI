package gov.iti.jets.NutriScan.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "nutrition_facts")
public class NutritionFact {
    @Id
    @Column(name = "scan_id", nullable = false)
    private UUID id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "scan_id", nullable = false)
    private Scan scans;

    @Size(max = 50)
    @Column(name = "serving_size", length = 50)
    private String servingSize;

    @Column(name = "calories_per_serving")
    private Integer caloriesPerServing;

    @Column(name = "sugar_g", precision = 6, scale = 2)
    private BigDecimal sugarG;

    @Column(name = "fat_g", precision = 6, scale = 2)
    private BigDecimal fatG;

    @Column(name = "saturated_fat_g", precision = 6, scale = 2)
    private BigDecimal saturatedFatG;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

}