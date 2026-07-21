package gov.iti.jets.NutriScan.service;

import gov.iti.jets.NutriScan.dto.DiseaseRequest;
import gov.iti.jets.NutriScan.dto.DiseaseResponse;
import gov.iti.jets.NutriScan.exception.DiseaseConflictException;
import gov.iti.jets.NutriScan.exception.DiseaseNotFoundException;
import gov.iti.jets.NutriScan.mapper.DiseaseMapper;
import gov.iti.jets.NutriScan.model.Disease;
import gov.iti.jets.NutriScan.repository.DiseaseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiseaseService {

    private final DiseaseRepository diseaseRepository;
    private final DiseaseMapper diseaseMapper;

    @Cacheable(value = "diseases", key = "#id")
    public DiseaseResponse findById(Integer id) {
        return diseaseRepository.findById(id)
            .map(diseaseMapper::toResponse)
            .orElseThrow(() -> new DiseaseNotFoundException("Disease not found with id: " + id));
    }

    @Cacheable(value = "diseases", key = "#name")
    public DiseaseResponse findByName(String name) {
        return diseaseRepository.findByName(name)
            .map(diseaseMapper::toResponse)
            .orElseThrow(
                () -> new DiseaseNotFoundException("Disease not found with name: " + name));
    }

    @Cacheable(value = "diseases", key = "'all'")
    public List<DiseaseResponse> findAll() {
        return diseaseMapper.toResponseList(diseaseRepository.findAll());
    }

    @CacheEvict(value = "diseases", allEntries = true)
    public DiseaseResponse save(DiseaseRequest diseaseRequest) {
        if (diseaseRepository.existsByName(diseaseRequest.name())) {
            throw new DiseaseConflictException(
                "Disease already exists with name: " + diseaseRequest.name());
        }
        Disease disease = diseaseMapper.toEntity(diseaseRequest);
        try {
            return diseaseMapper.toResponse(diseaseRepository.save(disease));
        } catch (DataIntegrityViolationException e) {
            throw new DiseaseConflictException(
                "Disease already exists with name: " + diseaseRequest.name());
        }
    }

    @CacheEvict(value = "diseases", allEntries = true)
    public List<DiseaseResponse> saveAll(List<DiseaseRequest> diseaseRequests) {

        Set<String> names = diseaseRequests.stream()
            .map(DiseaseRequest::name)
            .collect(Collectors.toSet());

        List<Disease> existing = diseaseRepository.findAllByNameIn(names);

        if (!existing.isEmpty()) {
            String existingNames = existing.stream()
                .map(Disease::getName)
                .collect(Collectors.joining(", "));

            throw new DiseaseConflictException(
                "Disease already exists with name(s): " + existingNames);
        }

        List<Disease> diseases = diseaseMapper.toEntityList(diseaseRequests);
        return diseaseMapper.toResponseList(diseaseRepository.saveAll(diseases));
    }

    @Transactional
    @CacheEvict(value = "diseases", allEntries = true)
    public void delete(Integer id) {
        diseaseRepository.deleteDiseaseById(id);
    }
}
