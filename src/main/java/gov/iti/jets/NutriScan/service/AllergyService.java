package gov.iti.jets.NutriScan.service;

import gov.iti.jets.NutriScan.dto.AllergyRequest;
import gov.iti.jets.NutriScan.dto.AllergyResponse;
import gov.iti.jets.NutriScan.exception.AllergyConflictException;
import gov.iti.jets.NutriScan.exception.AllergyNotFoundException;
import gov.iti.jets.NutriScan.mapper.AllergyMapper;
import gov.iti.jets.NutriScan.model.Allergy;
import gov.iti.jets.NutriScan.repository.AllergyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AllergyService {

    private final AllergyRepository allergyRepository;
    private final AllergyMapper allergyMapper;

    @Cacheable(value = "allergies", key = "#id")
    public AllergyResponse findById(Integer id) {
        return allergyRepository.findById(id)
            .map(allergyMapper::toResponse)
            .orElseThrow(() -> new AllergyNotFoundException("Allergy not found with id: " + id));
    }

    @Cacheable(value = "allergies", key = "#name")
    public AllergyResponse findByName(String name) {
        return allergyRepository.findByName(name)
            .map(allergyMapper::toResponse)
            .orElseThrow(
                () -> new AllergyNotFoundException("Allergy not found with name: " + name));
    }

    @Cacheable(value = "allergies", key = "'all'")
    public List<AllergyResponse> findAll() {
        return allergyMapper.toResponseList(allergyRepository.findAll());
    }

    @CacheEvict(value = "allergies", allEntries = true)
    public AllergyResponse save(AllergyRequest allergyRequest) {
        if (allergyRepository.existsByName(allergyRequest.name())) {
            throw new AllergyConflictException(
                "Allergy already exists with name: " + allergyRequest.name());
        }

        Allergy allergy = allergyMapper.toEntity(allergyRequest);
        return allergyMapper.toResponse(allergyRepository.save(allergy));
    }

    @CacheEvict(value = "allergies", allEntries = true)
    public List<AllergyResponse> saveAll(List<AllergyRequest> allergyRequests) {

        for (var allergyRequest : allergyRequests) {
            if (allergyRepository.existsByName(allergyRequest.name())) {
                throw new AllergyConflictException(
                    "Allergy already exists with name: " + allergyRequest.name());
            }
        }

        List<Allergy> allergies = allergyMapper.toEntityList(allergyRequests);
        return allergyMapper.toResponseList(allergyRepository.saveAll(allergies));
    }

    @CacheEvict(value = "allergies", allEntries = true)
    public void delete(Integer id) {
        allergyRepository.deleteById(id);
    }
}
