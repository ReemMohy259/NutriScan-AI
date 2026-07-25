package gov.iti.jets.NutriScan.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class DailyTrackingMealId implements Serializable {

    private Integer dailyId;

    private UUID scanId;
}