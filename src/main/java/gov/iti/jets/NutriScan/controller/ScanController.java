package gov.iti.jets.NutriScan.controller;

import gov.iti.jets.NutriScan.dto.ScanResultResponse;
import gov.iti.jets.NutriScan.dto.ScanSubmitResponse;
import gov.iti.jets.NutriScan.dto.ScanSummaryResponse;
import gov.iti.jets.NutriScan.service.ScanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/scans")
@RequiredArgsConstructor
public class ScanController {

    private final ScanService scanService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ScanSubmitResponse submitScan(@RequestParam("image") MultipartFile image) {
        return null;
    }

    @GetMapping("/{scanId}")
    public ScanResultResponse getScanResult(@PathVariable UUID scanId) {
        return null;
    }

    @GetMapping
    public Page<ScanSummaryResponse> getScanHistory(Pageable pageable) {
        return null;
    }
}
