package gov.iti.jets.NutriScan.dto.ai;

public record FamilyAlertDto(String targetProfile, FamilyMemberVerdict severity, String reason) {
}
