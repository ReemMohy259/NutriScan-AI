package gov.iti.jets.NutriScan.model;

import gov.iti.jets.NutriScan.dto.ai.FamilyMemberVerdict;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "scan_family_alert")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScanFamilyAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "scan_id", nullable = false)
    private Scan scan;

    @Size(max = 60)
    @Column(name = "target_profile", length = 60)
    private String targetProfile;

    @Enumerated(EnumType.STRING)
    @Column(name = "verdict", length = 20)
    private FamilyMemberVerdict verdict;

    @Column(name = "reason", length = Integer.MAX_VALUE)
    private String reason;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

}