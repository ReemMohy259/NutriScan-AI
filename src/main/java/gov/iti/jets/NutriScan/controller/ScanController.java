package gov.iti.jets.NutriScan.controller;

import gov.iti.jets.NutriScan.dto.ScanSubmitResponse;
import gov.iti.jets.NutriScan.dto.ai.ScanStatus;
import gov.iti.jets.NutriScan.exception.NoImageProvidedException;
import gov.iti.jets.NutriScan.service.ScanService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@RestController
@RequestMapping("/api/scans")
public class ScanController {

    private final ScanService scanService;

    public ScanController(ScanService scanService) {
        this.scanService = scanService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ScanSubmitResponse> scan(@RequestParam("image") MultipartFile image) {

        if (image == null || image.isEmpty())
            throw new NoImageProvidedException("Image is required");

        ScanSubmitResponse result = scanService.addNewScan();
        scanService.processScan(image);

        return ResponseEntity.accepted().body(result);
    }
}
