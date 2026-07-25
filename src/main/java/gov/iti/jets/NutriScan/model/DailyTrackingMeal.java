package gov.iti.jets.NutriScan.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "daily_tracking_meals")
public class DailyTrackingMeal {

    @EmbeddedId
    private DailyTrackingMealId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("dailyId")
    @JoinColumn(name = "daily_id", foreignKey = @ForeignKey(name = "fk_daily_daily_meals"))
    private DailyTracking dailyTracking;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("scanId")
    @JoinColumn(name = "scan_id", foreignKey = @ForeignKey(name = "fk_scans_daily_meals"))
    private Scan scan;

    @Column(name = "meal_cnt", nullable = false)
    private Integer mealCnt;

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof DailyTrackingMeal that))
            return false;

        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}