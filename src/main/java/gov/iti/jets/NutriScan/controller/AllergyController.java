package gov.iti.jets.NutriScan.controller;

import gov.iti.jets.NutriScan.dto.AllergyResponse;
import gov.iti.jets.NutriScan.ratelimit.RateLimit;
import gov.iti.jets.NutriScan.ratelimit.RateLimitKeyType;
import gov.iti.jets.NutriScan.service.AllergyService;
import lombok.RequiredArgsConstructor;
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

    @RateLimit(limit = 350, keyType = RateLimitKeyType.IP)
    @GetMapping
    public List<AllergyResponse> getAllAllergies() {
        return allergyService.findAll();
    }

    @RateLimit(limit = 350, keyType = RateLimitKeyType.IP)
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
