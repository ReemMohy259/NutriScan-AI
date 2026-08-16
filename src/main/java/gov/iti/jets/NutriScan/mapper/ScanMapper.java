package gov.iti.jets.NutriScan.mapper;

import gov.iti.jets.NutriScan.dto.ScanResultResponse;
import gov.iti.jets.NutriScan.dto.ScanSubmitResponse;
import gov.iti.jets.NutriScan.dto.ScanSummaryResponse;
import gov.iti.jets.NutriScan.dto.ai.FamilyAlertDto;
import gov.iti.jets.NutriScan.dto.ai.FlaggedIngredient;
import gov.iti.jets.NutriScan.dto.ai.FoodSafetyResponse;
import gov.iti.jets.NutriScan.dto.ai.ScanStatus;
import gov.iti.jets.NutriScan.model.Scan;
import gov.iti.jets.NutriScan.model.ScanFamilyAlert;
import gov.iti.jets.NutriScan.model.ScanFlaggedIngredient;
import gov.iti.jets.NutriScan.model.elasticsearch.ScanDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = NutritionFactMapper.class)
public interface ScanMapper {

    @Mapping(source = "id", target = "scanId")
    @Mapping(source = "status", target = "status")
    @Mapping(target = "foodSafetyResponse", expression = "java(mapScanToFoodSafetyResponse(scan))")
    @Mapping(source = "nutritionFact", target = "nutritionFacts")
    ScanResultResponse toResultResponse(Scan scan);

    @Mapping(source = "id", target = "scanId")
    @Mapping(source = "status", target = "status")
    ScanSubmitResponse toSubmitResponse(Scan scan);

    default FoodSafetyResponse mapScanToFoodSafetyResponse(Scan scan) {
        if (scan == null) {
            return null;
        }

        return new FoodSafetyResponse(
            scan.getVerdict(),
            mapScanFlaggedIngredientsToFlaggedIngredients(scan.getScanFlaggedIngredients()),
            scan.getSummary(),
            mapScanFamilyAlert(scan.getFamilyAlerts()));
    }

    default List<FlaggedIngredient> mapScanFlaggedIngredientsToFlaggedIngredients(
        Set<ScanFlaggedIngredient> set) {

        if (set == null) {
            return List.of();
        }

        return set.stream().map(this::mapScanFlaggedIngredientToFlaggedIngredient).toList();
    }

    default List<FamilyAlertDto> mapScanFamilyAlert(Set<ScanFamilyAlert> set) {

        if (set == null) {
            return List.of();
        }

        return set.stream()
            .map(
                scanFamilyAlert -> new FamilyAlertDto(
                    scanFamilyAlert.getTargetProfile(),
                    scanFamilyAlert.getVerdict(),
                    scanFamilyAlert.getReason()))
            .toList();

    }

    default FlaggedIngredient mapScanFlaggedIngredientToFlaggedIngredient(
        ScanFlaggedIngredient ingredient) {

        if (ingredient == null) {
            return null;
        }

        FlaggedIngredient.FlagType flagType = null;

        if (ingredient.getType() != null) {
            if ("CHRONIC_CONDITION".equalsIgnoreCase(ingredient.getType())
                || "CONDITION".equalsIgnoreCase(ingredient.getType())) {
                flagType = FlaggedIngredient.FlagType.CHRONIC_CONDITION;
            } else if ("ALLERGY".equalsIgnoreCase(ingredient.getType())) {
                flagType = FlaggedIngredient.FlagType.ALLERGY;
            }
        }

        List<String> nameList = ingredient.getConditionName() != null
            ? List.of(ingredient.getConditionName())
            : List.of();

        return new FlaggedIngredient(
            ingredient.getIngredientName(),
            ingredient.getReason(),
            flagType,
            nameList);
    }

    default ScanFlaggedIngredient mapFlaggedIngredientToScanFlaggedIngredient(
        FlaggedIngredient flaggedIngredient) {
        if (flaggedIngredient == null) {
            return null;
        }
        ScanFlaggedIngredient entity = new ScanFlaggedIngredient();
        entity.setIngredientName(flaggedIngredient.ingredient());
        entity.setReason(flaggedIngredient.reason());

        if (flaggedIngredient.type() != null) {
            if (flaggedIngredient.type() == FlaggedIngredient.FlagType.CHRONIC_CONDITION) {
                entity.setType("CHRONIC_CONDITION");
            } else {
                entity.setType(flaggedIngredient.type().name());
            }
        }

        if (flaggedIngredient.name() != null && !flaggedIngredient.name().isEmpty()) {
            entity.setConditionName(flaggedIngredient.name().get(0));
        }

        return entity;
    }

    default ScanStatus mapStringToScanStatus(String status) {
        if (status == null) {
            return null;
        }

        try {
            return ScanStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return ScanStatus.PROCESSING;
        }
    }

    default ScanSummaryResponse toSummaryResponse(Scan scan) {
        if (scan == null) {
            return null;
        }

        Integer calories = null;
        if (scan.getNutritionFact() != null) {
            calories = scan.getNutritionFact().getCalories();
        }

        return new ScanSummaryResponse(
            scan.getId(),
            scan.getImageUrl(),
            scan.getVerdict(),
            scan.getScannedAt(),
            scan.getProductName(),
            calories,
            scan.getStatus());
    }

    default String mapProductName(String name) {
        return name == null ? "" : name;
    }

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "scannedAt", target = "scannedAt")
    @Mapping(source = "status", target = "scanStatus")
    @Mapping(source = "productName", target = "productName")
    ScanDocument toDocument(Scan scan);
}