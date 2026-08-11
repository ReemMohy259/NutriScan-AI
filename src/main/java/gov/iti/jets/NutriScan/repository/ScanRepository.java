package gov.iti.jets.NutriScan.repository;

import gov.iti.jets.NutriScan.dto.ScanSummaryResponse;
import gov.iti.jets.NutriScan.model.Scan;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScanRepository extends JpaRepository<Scan, UUID>, JpaSpecificationExecutor<Scan> {

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
            s.scannedAt,
            s.productName,
            nf.calories,
                    s.status
        )
            from Scan s
                left join s.nutritionFact nf
        where s.user.id = :userId
        order by s.scannedAt desc
        """)
    Page<ScanSummaryResponse> findSummaryByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("""
        select new gov.iti.jets.NutriScan.dto.ScanSummaryResponse(
            s.id,
            s.imageUrl,
            s.verdict,
            s.scannedAt,
            s.productName,
            nf.calories,
            s.status
        )
        from Scan s
            left join s.nutritionFact nf
        where s.user.id = :userId
          and s.id in :ids
        """)
    List<ScanSummaryResponse> findSummaryByUserIdAndIds(UUID userId, Collection<UUID> ids);

    @Query("""
        select new gov.iti.jets.NutriScan.dto.ScanSummaryResponse(
            s.id,
            s.imageUrl,
            s.verdict,
            s.scannedAt,
            s.productName,
            nf.calories,
                    s.status
        )
            from Scan s
                left join s.nutritionFact nf
        where s.user.id = :userId and s.favorite=true
        order by s.scannedAt desc
        """)
    Page<ScanSummaryResponse> findFavoritesByUserId(
        @Param("userId") UUID userId,
        Pageable pageable);

    @Query("select s from Scan s where s.id = :id and s.user.id = :userId")
    Optional<Scan> findByIdAndUserId(UUID id, UUID userId);

    @NonNull
    Page<Scan> findAll(@NonNull Specification<Scan> specification, @NonNull Pageable pageable);

    void deleteScanByIdAndUserId(UUID scanId, UUID userID);

    @Query("delete from Scan s where s.id = :id and s.user.id = :userId")
    @Modifying
    void deleteByIdAndUserId(UUID id, UUID userId);
}
