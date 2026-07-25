package gov.iti.jets.NutriScan.controller;

import gov.iti.jets.NutriScan.dto.DailyTrackingMealRequest;
import gov.iti.jets.NutriScan.dto.DailyTrackingMealResponse;
import gov.iti.jets.NutriScan.dto.DailyTrackingRequest;
import gov.iti.jets.NutriScan.dto.DailyTrackingResponse;
import gov.iti.jets.NutriScan.dto.DailyTrackingSummaryResponse;
import gov.iti.jets.NutriScan.service.DailyTrackingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/daily-tracking")
@RequiredArgsConstructor
public class DailyTrackingController {

    private final DailyTrackingService dailyTrackingService;

    @GetMapping("/today")
    public DailyTrackingResponse getToday(@AuthenticationPrincipal Jwt jwt) {
        return dailyTrackingService.getOrCreateToday(jwt);
    }

    @GetMapping("/{date}")
    public DailyTrackingResponse getByDate(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return dailyTrackingService.getByDate(jwt, date);
    }

    @GetMapping
    public Page<DailyTrackingSummaryResponse> getAll(
        @AuthenticationPrincipal Jwt jwt,
        @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page must be greater than or equal to 0") int page,
        @RequestParam(defaultValue = "20") @Min(value = 1, message = "Size must be at least 1") @Max(value = 100, message = "Size must not exceed 100") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));
        return dailyTrackingService.getAll(jwt, pageable);
    }

    @PatchMapping("/{date}")
    public DailyTrackingResponse updateTracking(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @Valid @RequestBody DailyTrackingRequest request) {
        return dailyTrackingService.updateTracking(jwt, date, request);
    }

    @PostMapping("/{date}/meals")
    public ResponseEntity<DailyTrackingMealResponse> addMeal(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @Valid @RequestBody DailyTrackingMealRequest request) {
        DailyTrackingMealResponse response = dailyTrackingService.addMeal(jwt, date, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{date}/meals/{scanId}")
    public DailyTrackingMealResponse updateMeal(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @PathVariable UUID scanId,
        @RequestParam @Min(value = 1, message = "Meal count must be at least 1") Integer mealCnt) {
        return dailyTrackingService.updateMeal(jwt, date, scanId, mealCnt);
    }

    @DeleteMapping("/{date}/meals/{scanId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMeal(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @PathVariable UUID scanId) {
        dailyTrackingService.removeMeal(jwt, date, scanId);
    }

    @DeleteMapping("/{date}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTracking(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        dailyTrackingService.deleteTracking(jwt, date);
    }
}