package gov.iti.jets.NutriScan.controller;

import gov.iti.jets.NutriScan.dto.DiseaseResponse;
import gov.iti.jets.NutriScan.service.DiseaseService;
import lombok.RequiredArgsConstructor;
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
    public List<DiseaseResponse> getAllDiseases() {
        return diseaseService.findAll();
    }

    @GetMapping("/{id}")
    public DiseaseResponse getDiseaseById(@PathVariable Integer id) {
        return diseaseService.findById(id);
    }

    // @PostMapping
    // @Operation(hidden = true)
    // public List<DiseaseResponse> addDisease(@RequestBody List<@Valid
    // DiseaseRequest> diseases) {
    // return diseaseService.saveAll(diseases);
    // }
    //
    // @DeleteMapping("/{id}")
    // @Operation(hidden = true)
    // public ResponseEntity<Void> deleteDisease(@PathVariable Integer id) {
    // diseaseService.delete(id);
    // return ResponseEntity.noContent().build();
    // }
}
