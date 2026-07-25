package gov.iti.jets.NutriScan.service;

import gov.iti.jets.NutriScan.dto.DailyTrackingMealRequest;
import gov.iti.jets.NutriScan.dto.DailyTrackingMealResponse;
import gov.iti.jets.NutriScan.dto.DailyTrackingRequest;
import gov.iti.jets.NutriScan.dto.DailyTrackingResponse;
import gov.iti.jets.NutriScan.dto.DailyTrackingSummaryResponse;
import gov.iti.jets.NutriScan.exception.DailyTrackingMealNotFoundException;
import gov.iti.jets.NutriScan.exception.DailyTrackingNotFoundException;
import gov.iti.jets.NutriScan.exception.ScanNotFoundException;
import gov.iti.jets.NutriScan.mapper.DailyTrackingMapper;
import gov.iti.jets.NutriScan.mapper.DailyTrackingMealMapper;
import gov.iti.jets.NutriScan.model.DailyTracking;
import gov.iti.jets.NutriScan.model.DailyTrackingMeal;
import gov.iti.jets.NutriScan.model.DailyTrackingMealId;
import gov.iti.jets.NutriScan.model.Scan;
import gov.iti.jets.NutriScan.model.User;
import gov.iti.jets.NutriScan.repository.DailyTrackingMealRepository;
import gov.iti.jets.NutriScan.repository.DailyTrackingRepository;
import gov.iti.jets.NutriScan.repository.ScanRepository;
import gov.iti.jets.NutriScan.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DailyTrackingService {

    private final DailyTrackingRepository dailyTrackingRepository;
    private final DailyTrackingMealRepository dailyTrackingMealRepository;
    private final ScanRepository scanRepository;
    private final UserRepository userRepository;
    private final DailyTrackingMapper dailyTrackingMapper;
    private final DailyTrackingMealMapper dailyTrackingMealMapper;

    @Transactional
    public DailyTrackingResponse getOrCreateToday(Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        LocalDate today = LocalDate.now();

        DailyTracking dailyTracking = dailyTrackingRepository.findByUserIdAndDate(userId, today)
            .orElseGet(() -> createDailyTracking(userId, today));

        return dailyTrackingMapper.toResponse(dailyTracking);
    }

    public DailyTrackingResponse getByDate(Jwt jwt, LocalDate date) {
        UUID userId = UUID.fromString(jwt.getSubject());

        DailyTracking dailyTracking = dailyTrackingRepository
            .findByIdWithMealsAndScans(
                dailyTrackingRepository.findByUserIdAndDate(userId, date)
                    .orElseThrow(
                        () -> new DailyTrackingNotFoundException(
                            "Daily tracking not found for date: " + date))
                    .getId(),
                userId)
            .orElseThrow(
                () -> new DailyTrackingNotFoundException(
                    "Daily tracking not found for date: " + date));

        return dailyTrackingMapper.toResponse(dailyTracking);
    }

    public Page<DailyTrackingSummaryResponse> getAll(Jwt jwt, Pageable pageable) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return dailyTrackingRepository.findSummaryByUserId(userId, pageable);
    }

    @Transactional
    public DailyTrackingResponse updateTracking(
        Jwt jwt,
        LocalDate date,
        DailyTrackingRequest request) {
        UUID userId = UUID.fromString(jwt.getSubject());

        DailyTracking dailyTracking = dailyTrackingRepository.findByUserIdAndDate(userId, date)
            .orElseThrow(
                () -> new DailyTrackingNotFoundException(
                    "Daily tracking not found for date: " + date));

        if (request.targetWaterCnt() != null) {
            dailyTracking.setTargetWaterCnt(request.targetWaterCnt());
        }
        if (request.waterCnt() != null) {
            dailyTracking.setWaterCnt(request.waterCnt());
        }
        if (request.stepsCnt() != null) {
            dailyTracking.setStepsCnt(request.stepsCnt());
        }

        return dailyTrackingMapper.toResponse(dailyTracking);
    }

    @Transactional
    public DailyTrackingMealResponse addMeal(
        Jwt jwt,
        LocalDate date,
        DailyTrackingMealRequest request) {
        UUID userId = UUID.fromString(jwt.getSubject());

        DailyTracking dailyTracking = dailyTrackingRepository.findByUserIdAndDate(userId, date)
            .orElseGet(() -> createDailyTracking(userId, date));

        // Verify scan exists and belongs to user
        Scan scan = scanRepository.findById(request.scanId())
            .orElseThrow(
                () -> new ScanNotFoundException("Scan not found with id: " + request.scanId()));

        if (!scan.getUser().getId().equals(userId)) {
            throw new ScanNotFoundException("Scan not found with id: " + request.scanId());
        }

        // Check if meal already exists
        if (dailyTrackingMealRepository
            .findByIdDailyIdAndIdScanId(dailyTracking.getId(), request.scanId())
            .isPresent()) {
            throw new IllegalArgumentException("Meal already exists for this scan");
        }

        DailyTrackingMeal meal = DailyTrackingMeal.builder()
            .id(new DailyTrackingMealId(dailyTracking.getId(), request.scanId()))
            .dailyTracking(dailyTracking)
            .scan(scan)
            .mealCnt(request.mealCnt())
            .build();

        dailyTrackingMealRepository.save(meal);

        return dailyTrackingMealMapper.toResponse(meal);
    }

    @Transactional
    public DailyTrackingMealResponse updateMeal(
        Jwt jwt,
        LocalDate date,
        UUID scanId,
        Integer mealCnt) {
        UUID userId = UUID.fromString(jwt.getSubject());

        DailyTracking dailyTracking = dailyTrackingRepository.findByUserIdAndDate(userId, date)
            .orElseThrow(
                () -> new DailyTrackingNotFoundException(
                    "Daily tracking not found for date: " + date));

        DailyTrackingMeal meal = dailyTrackingMealRepository
            .findByIdDailyIdAndIdScanId(dailyTracking.getId(), scanId)
            .orElseThrow(
                () -> new DailyTrackingMealNotFoundException("Meal not found for scan: " + scanId));

        meal.setMealCnt(mealCnt);

        return dailyTrackingMealMapper.toResponse(meal);
    }

    @Transactional
    public void removeMeal(Jwt jwt, LocalDate date, UUID scanId) {
        UUID userId = UUID.fromString(jwt.getSubject());

        DailyTracking dailyTracking = dailyTrackingRepository.findByUserIdAndDate(userId, date)
            .orElseThrow(
                () -> new DailyTrackingNotFoundException(
                    "Daily tracking not found for date: " + date));

        DailyTrackingMeal meal = dailyTrackingMealRepository
            .findByIdDailyIdAndIdScanId(dailyTracking.getId(), scanId)
            .orElseThrow(
                () -> new DailyTrackingMealNotFoundException("Meal not found for scan: " + scanId));

        dailyTrackingMealRepository.delete(meal);
    }

    @Transactional
    public void deleteTracking(Jwt jwt, LocalDate date) {
        UUID userId = UUID.fromString(jwt.getSubject());

        DailyTracking dailyTracking = dailyTrackingRepository.findByUserIdAndDate(userId, date)
            .orElseThrow(
                () -> new DailyTrackingNotFoundException(
                    "Daily tracking not found for date: " + date));

        dailyTrackingRepository.delete(dailyTracking);
    }

    private DailyTracking createDailyTracking(UUID userId, LocalDate date) {
        User user = userRepository.getReferenceById(userId);
        DailyTracking dailyTracking = DailyTracking.builder().user(user).date(date).build();
        return dailyTrackingRepository.save(dailyTracking);
    }
}