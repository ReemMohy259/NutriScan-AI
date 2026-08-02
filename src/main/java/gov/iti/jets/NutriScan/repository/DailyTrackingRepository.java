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
        select distinct dt
        from DailyTracking dt
        left join fetch dt.meals m
        left join fetch m.scan s
        left join fetch s.nutritionFact
        where dt.user.id = :userId
          and dt.date = :date
        """)
    Optional<DailyTracking> findWithFetchingByUserIdAndDate(
        @Param("userId") UUID userId,
        @Param("date") LocalDate date);

    @Query("""
        select new gov.iti.jets.NutriScan.dto.DailyTrackingSummaryResponse(
            dt.id,
            dt.date,
            dt.targetWaterCnt,
            dt.waterCnt,
            dt.stepsCnt,
            dt.stepsKcal,
            dt.exerciseKcal,
            dt.exerciseMin,
            cast(coalesce((
                select sum(m.mealCnt * nf.calories)
                from DailyTrackingMeal m
                join m.scan.nutritionFact nf
                where m.dailyTracking = dt
                and nf.calories is not null
            ), 0) as long),
            size(dt.meals)
        )
        from DailyTracking dt
        where dt.user.id = :userId
        order by dt.date desc
        """)
    Page<DailyTrackingSummaryResponse> findSummaryByUserId(
        @Param("userId") UUID userId,
        Pageable pageable);

    void deleteByUserIdAndDate(UUID userId, LocalDate date);
}