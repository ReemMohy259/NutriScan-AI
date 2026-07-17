package gov.iti.jets.NutriScan.controller;

import gov.iti.jets.NutriScan.dto.AllergyResponse;
import gov.iti.jets.NutriScan.service.AllergyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/allergies")
@RequiredArgsConstructor
public class AllergyController {

    private final AllergyService allergyService;

    @GetMapping
    public ResponseEntity<List<AllergyResponse>> getAllAllergies() {
        return ResponseEntity.ok(allergyService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AllergyResponse> getAllergyById(@PathVariable Integer id) {
        return ResponseEntity.ok(allergyService.findById(id));
    }
}
