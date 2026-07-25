package gov.iti.jets.NutriScan.repository;

import gov.iti.jets.NutriScan.model.DailyTrackingMeal;
import gov.iti.jets.NutriScan.model.DailyTrackingMealId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}