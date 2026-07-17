package gov.iti.jets.NutriScan.service;

import gov.iti.jets.NutriScan.dto.DiseaseRequest;
import gov.iti.jets.NutriScan.dto.DiseaseResponse;
import gov.iti.jets.NutriScan.exception.DiseaseConflictException;
import gov.iti.jets.NutriScan.exception.DiseaseNotFoundException;
import gov.iti.jets.NutriScan.mapper.DiseaseMapper;
import gov.iti.jets.NutriScan.model.Disease;
import gov.iti.jets.NutriScan.repository.DiseaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiseaseService {

    private final DiseaseRepository diseaseRepository;
    private final DiseaseMapper diseaseMapper;

    public DiseaseResponse findById(Integer id) {
        return diseaseRepository.findById(id)
            .map(diseaseMapper::toResponse)
            .orElseThrow(() -> new DiseaseNotFoundException("Disease not found with id: " + id));
    }

    public DiseaseResponse findByName(String name) {
        return diseaseRepository.findByName(name)
            .map(diseaseMapper::toResponse)
            .orElseThrow(
                () -> new DiseaseNotFoundException("Disease not found with name: " + name));
    }

    public List<DiseaseResponse> findAll() {
        return diseaseMapper.toResponseList(diseaseRepository.findAll());
    }

    public DiseaseResponse save(DiseaseRequest diseaseRequest) {
        if (diseaseRepository.existsByName(diseaseRequest.name())) {
            throw new DiseaseConflictException(
                "Disease already exists with name: " + diseaseRequest.name());
        }

        Disease disease = diseaseMapper.toEntity(diseaseRequest);
        return diseaseMapper.toResponse(diseaseRepository.save(disease));
    }

    public List<DiseaseResponse> saveAll(List<DiseaseRequest> diseaseRequests) {
        for (var diseaseRequest : diseaseRequests) {
            if (diseaseRepository.existsByName(diseaseRequest.name())) {
                throw new DiseaseConflictException(
                    "Disease already exists with name: " + diseaseRequest.name());
            }
        }

        List<Disease> diseases = diseaseMapper.toEntityList(diseaseRequests);
        return diseaseMapper.toResponseList(diseaseRepository.saveAll(diseases));
    }

    public void delete(Integer id) {
        diseaseRepository.deleteById(id);
    }
}
