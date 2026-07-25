package gov.iti.jets.NutriScan.repository;

import gov.iti.jets.NutriScan.dto.DailyTrackingSummaryResponse;
import gov.iti.jets.NutriScan.model.DailyTracking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface DailyTrackingRepository extends JpaRepository<DailyTracking, Integer> {

    Optional<DailyTracking> findByUserIdAndDate(
        @Param("userId") UUID userId,
        @Param("date") LocalDate date);

    @Query("""
        select new gov.iti.jets.NutriScan.dto.DailyTrackingSummaryResponse(
            dt.id, dt.date, dt.targetWaterCnt, dt.waterCnt, dt.stepsCnt, size(dt.meals))
        from DailyTracking dt
        where dt.user.id = :userId
        order by dt.date desc
        """)
    Page<DailyTrackingSummaryResponse> findSummaryByUserId(
        @Param("userId") UUID userId,
        Pageable pageable);

    @Query("""
        select distinct dt
        from DailyTracking dt
        left join fetch dt.meals m
        left join fetch m.scan s
        where dt.id = :id and dt.user.id = :userId
        """)
    Optional<DailyTracking> findByIdWithMealsAndScans(
        @Param("id") Integer id,
        @Param("userId") UUID userId);
}