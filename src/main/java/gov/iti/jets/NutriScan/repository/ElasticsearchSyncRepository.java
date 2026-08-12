package gov.iti.jets.NutriScan.repository;

import gov.iti.jets.NutriScan.model.ElasticsearchSync;
import gov.iti.jets.NutriScan.model.EntityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ElasticsearchSyncRepository extends JpaRepository<ElasticsearchSync, UUID> {

    List<ElasticsearchSync> findTop500ByProcessedFalseOrderByCreatedAtAsc();

    @Modifying
    @Query("""
        DELETE FROM ElasticsearchSync s
        WHERE s.processed = true
        AND s.processedAt < :time
        """)
    void deleteProcessedOlderThan(Instant time);

    Optional<ElasticsearchSync> findByEntityIdAndEntityType(UUID uuid, EntityType entityType);
}