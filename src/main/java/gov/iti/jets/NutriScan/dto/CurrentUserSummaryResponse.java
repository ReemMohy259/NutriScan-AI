package gov.iti.jets.NutriScan.dto;

import lombok.Builder;
import java.util.UUID;

@Builder
public record CurrentUserSummaryResponse(

    UUID id,

    String firstName,

    String lastName,

    Integer dailyStreak,

    String email,

    String username,

    String imageUrl) {

}
