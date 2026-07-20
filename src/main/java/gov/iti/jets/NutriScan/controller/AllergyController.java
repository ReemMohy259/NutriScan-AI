package gov.iti.jets.NutriScan.controller;

import gov.iti.jets.NutriScan.dto.AllergyRequest;
import gov.iti.jets.NutriScan.dto.AllergyResponse;
import gov.iti.jets.NutriScan.service.AllergyService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/allergies")
@RequiredArgsConstructor
public class AllergyController {

    private final AllergyService allergyService;

    @GetMapping
    public List<AllergyResponse> getAllAllergies() {
        return allergyService.findAll();
    }

    @GetMapping("/{id}")
    public AllergyResponse getAllergyById(@PathVariable Integer id) {
        return allergyService.findById(id);
    }

    // @PostMapping
    // @Operation(hidden = true)
    // public List<AllergyResponse> addAllergies(@RequestBody List<@Valid
    // AllergyRequest> allergies) {
    // return allergyService.saveAll(allergies);
    // }
    //
    // @DeleteMapping("/{id}")
    // @Operation(hidden = true)
    // public ResponseEntity<Void> deleteAllergy(@PathVariable Integer id) {
    // allergyService.delete(id);
    // return ResponseEntity.noContent().build();
    // }
}
