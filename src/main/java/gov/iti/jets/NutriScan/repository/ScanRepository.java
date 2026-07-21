package gov.iti.jets.NutriScan.repository;

import gov.iti.jets.NutriScan.dto.ScanSummaryResponse;
import gov.iti.jets.NutriScan.model.Scan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScanRepository extends JpaRepository<Scan, UUID> {

    List<Scan> findByStatus(String status);

    @Query("""
            select distinct s
            from Scan s
            left join fetch s.nutritionFact
            left join fetch s.scanFlaggedIngredients
            where s.id = :id and s.user.id = :userId
        """)
    Optional<Scan> findByIdWithDetails(UUID id, UUID userId);

    @Query("""
        select new gov.iti.jets.NutriScan.dto.ScanSummaryResponse(
            s.id,
            s.imageUrl,
            s.verdict,
            s.scannedAt
        )
        from Scan s
        where s.user.id = :userId
        order by s.scannedAt desc
        """)
    Page<ScanSummaryResponse> findSummaryByUserId(@Param("userId") UUID userId, Pageable pageable);
}
