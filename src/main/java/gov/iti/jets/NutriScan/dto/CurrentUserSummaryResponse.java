package gov.iti.jets.NutriScan.dto;

import java.util.UUID;

public record CurrentUserSummaryResponse(

    UUID id,

    String firstName,

    String lastName,

    Integer dailyStreak,

    String email,

    String username,

    String imageUrl) {

}
