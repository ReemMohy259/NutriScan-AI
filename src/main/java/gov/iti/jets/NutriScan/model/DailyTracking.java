package gov.iti.jets.NutriScan.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "daily_tracking", uniqueConstraints = @UniqueConstraint(name = "uk_daily_user_date", columnNames = {
        "user_id", "date"}))
public class DailyTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_users_daily"))
    private User user;

    @Column(nullable = false)
    private LocalDate date;

    @Builder.Default
    @Column(name = "target_water_cnt", nullable = false)
    private Integer targetWaterCnt = 8;

    @Builder.Default
    @Column(name = "water_cnt", nullable = false)
    private Integer waterCnt = 0;

    @Builder.Default
    @Column(name = "steps_cnt")
    private Integer stepsCnt = 0;

    @Builder.Default
    @OneToMany(mappedBy = "dailyTracking", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DailyTrackingMeal> meals = new HashSet<>();

    public void addMeal(DailyTrackingMeal meal) {
        meals.add(meal);
        meal.setDailyTracking(this);
    }

    public void removeMeal(DailyTrackingMeal meal) {
        meals.remove(meal);
        meal.setDailyTracking(null);
    }
}