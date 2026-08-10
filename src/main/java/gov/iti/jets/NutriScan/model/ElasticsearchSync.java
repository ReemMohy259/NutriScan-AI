package gov.iti.jets.NutriScan.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "elasticsearch_sync")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElasticsearchSync {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntityType entityType;

    @Column(nullable = false)
    private UUID entityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SyncOperation operation;

    @CreationTimestamp
    private Instant createdAt;

    private boolean processed;

    private Instant processedAt;

    private int retryCount;

    @Column(length = 5000)
    private String lastError;
}