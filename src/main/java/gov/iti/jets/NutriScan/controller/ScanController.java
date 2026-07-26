package gov.iti.jets.NutriScan.controller;

import gov.iti.jets.NutriScan.dto.ScanResultResponse;
import gov.iti.jets.NutriScan.dto.ScanSubmitResponse;
import gov.iti.jets.NutriScan.dto.ScanSummaryResponse;
import gov.iti.jets.NutriScan.dto.UpdateScanDto;
import gov.iti.jets.NutriScan.exception.ImageTooLargeException;
import gov.iti.jets.NutriScan.exception.InvalidImageException;
import gov.iti.jets.NutriScan.exception.NoImageProvidedException;
import gov.iti.jets.NutriScan.service.ScanService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/scans")
@RequiredArgsConstructor
public class ScanController {

    private final ScanService scanService;
    private static final long MAX_IMAGE_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<ScanSubmitResponse> scan(
        @AuthenticationPrincipal Jwt jwt,
        @RequestParam("image") MultipartFile image) {

        if (image == null || image.isEmpty())
            throw new NoImageProvidedException("Image is required");

        String contentType = image.getContentType();

        if (contentType == null || !contentType.startsWith("image/"))
            throw new InvalidImageException("Only image files are allowed");

        if (image.getSize() > MAX_IMAGE_SIZE_BYTES)
            throw new ImageTooLargeException("Image size must not exceed 5 MB");

        ScanSubmitResponse result = scanService.addNewScan(jwt, image);

        try {
            byte[] bytes = image.getBytes();
            scanService.processScan(
                jwt,
                result.scanId(),
                bytes,
                image.getContentType(),
                image.getOriginalFilename());
        } catch (IOException e) {
            throw new InvalidImageException("Failed To Read Image");
        }

        return ResponseEntity.accepted().body(result);
    }

    @GetMapping("/{scanId}")
    public ScanResultResponse getScanResult(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID scanId) {
        return scanService.findById(scanId, jwt);
    }

    @PatchMapping("/{scanId}")
    public ResponseEntity<ScanResultResponse> updateScanName(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID scanId,
        @Valid @RequestBody UpdateScanDto request) {

        ScanResultResponse scan = scanService
            .updateScan(scanId, request.name(), request.favorite(), jwt);
        return ResponseEntity.ok().body(scan);
    }

    @GetMapping
    public Page<ScanSummaryResponse> getScans(
        @AuthenticationPrincipal Jwt jwt,
        @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page must be greater than or equal to 0") int page,
        @Min(value = 1, message = "Size must be at least 1") @Max(value = 100, message = "Size must not exceed 100") @RequestParam(defaultValue = "20") int size) {

        Pageable validated = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        return scanService.findByUserId(jwt, validated);
    }
}
