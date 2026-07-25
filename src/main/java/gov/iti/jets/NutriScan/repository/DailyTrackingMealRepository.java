package gov.iti.jets.NutriScan.repository;

import gov.iti.jets.NutriScan.model.DailyTrackingMeal;
import gov.iti.jets.NutriScan.model.DailyTrackingMealId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface DailyTrackingMealRepository
    extends
        JpaRepository<DailyTrackingMeal, DailyTrackingMealId> {

    @Query("""
        select dtm
        from DailyTrackingMeal dtm
        left join fetch dtm.scan s
        where dtm.id.dailyId = :dailyId and dtm.id.scanId = :scanId
        """)
    Optional<DailyTrackingMeal> findByIdDailyIdAndIdScanId(
        @Param("dailyId") Integer dailyId,
        @Param("scanId") UUID scanId);

    @Query("""
        SELECT m
        FROM DailyTrackingMeal m
        JOIN FETCH m.scan s
        LEFT JOIN FETCH s.nutritionFact
        WHERE m.dailyTracking.user.id = :userId
          AND m.dailyTracking.date = :date
          AND s.id = :scanId
        """)
    Optional<DailyTrackingMeal> findMealWithScansByUserIdAndDateAndScanId(
        UUID userId,
        LocalDate date,
        UUID scanId);

    @Modifying
    @Transactional
    @Query("""
            DELETE FROM DailyTrackingMeal m
            WHERE m.dailyTracking.user.id = :userId
              AND m.dailyTracking.date = :date
              AND m.scan.id = :scanId
        """)
    void deleteByUserIdAndDateAndScanId(
        @Param("userId") UUID userId,
        @Param("date") LocalDate date,
        @Param("scanId") UUID scanId);

}