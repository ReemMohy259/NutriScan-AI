package gov.iti.jets.NutriScan.controller;

import gov.iti.jets.NutriScan.dto.DiseaseRequest;
import gov.iti.jets.NutriScan.dto.DiseaseResponse;
import gov.iti.jets.NutriScan.service.DiseaseService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/diseases")
@RequiredArgsConstructor
public class DiseaseController {

    private final DiseaseService diseaseService;

    @GetMapping
    public List<DiseaseResponse> getAllDiseases() {
        return diseaseService.findAll();
    }

    @GetMapping("/{id}")
    public DiseaseResponse getDiseaseById(@PathVariable Integer id) {
        return diseaseService.findById(id);
    }

    @PostMapping
    // @PreAuthorize("hasRole('ADMIN')")
    @Operation(hidden = true)
    public List<DiseaseResponse> addAllergies(@RequestBody List<@Valid DiseaseRequest> diseases) {
        return diseaseService.saveAll(diseases);
    }
}
