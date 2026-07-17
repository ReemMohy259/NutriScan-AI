package gov.iti.jets.NutriScan.service;

import gov.iti.jets.NutriScan.dto.AllergyRequest;
import gov.iti.jets.NutriScan.dto.AllergyResponse;
import gov.iti.jets.NutriScan.exception.AllergyNotFoundException;
import gov.iti.jets.NutriScan.mapper.AllergyMapper;
import gov.iti.jets.NutriScan.model.Allergy;
import gov.iti.jets.NutriScan.repository.AllergyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AllergyService {

    private final AllergyRepository allergyRepository;
    private final AllergyMapper allergyMapper;

    public AllergyResponse findById(Integer id) {
        return allergyRepository.findById(id)
            .map(allergyMapper::toResponse)
            .orElseThrow(() -> new AllergyNotFoundException("Allergy not found with id: " + id));
    }

    public AllergyResponse findByName(String name) {
        return allergyRepository.findByName(name)
            .map(allergyMapper::toResponse)
            .orElseThrow(
                () -> new AllergyNotFoundException("Allergy not found with name: " + name));
    }

    public List<AllergyResponse> findAll() {
        return allergyMapper.toResponseList(allergyRepository.findAll());
    }

    public AllergyResponse save(AllergyRequest allergyRequest) {
        Allergy allergy = allergyMapper.toEntity(allergyRequest);
        return allergyMapper.toResponse(allergyRepository.save(allergy));
    }

    public List<AllergyResponse> saveAll(List<AllergyRequest> allergyRequests) {
        List<Allergy> allergies = allergyMapper.toEntityList(allergyRequests);
        return allergyMapper.toResponseList(allergyRepository.saveAll(allergies));
    }

    public void delete(Integer id) {
        allergyRepository.deleteById(id);
    }
}
