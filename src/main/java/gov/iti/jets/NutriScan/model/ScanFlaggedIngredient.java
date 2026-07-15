package gov.iti.jets.NutriScan.model;

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

@Getter
@Setter
@Entity
@Table(name = "scan_flagged_ingredients")
public class ScanFlaggedIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "scan_id", nullable = false)
    private Scan scan;

    @Size(max = 30)
    @Column(name = "type", length = 30)
    private String type;

    @Size(max = 150)
    @Column(name = "condition_name", length = 150)
    private String conditionName;

    @Size(max = 150)
    @NotNull
    @Column(name = "ingredient_name", nullable = false, length = 150)
    private String ingredientName;

    @Column(name = "reason", length = Integer.MAX_VALUE)
    private String reason;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;


}