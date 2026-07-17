package gov.iti.jets.NutriScan.controller;

import gov.iti.jets.NutriScan.dto.DiseaseResponse;
import gov.iti.jets.NutriScan.service.DiseaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/diseases")
@RequiredArgsConstructor
public class DiseaseController {

    private final DiseaseService diseaseService;

    @GetMapping
    public ResponseEntity<List<DiseaseResponse>> getAllDiseases() {
        return ResponseEntity.ok(diseaseService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiseaseResponse> getDiseaseById(@PathVariable Integer id) {
        return ResponseEntity.ok(diseaseService.findById(id));
    }
}
