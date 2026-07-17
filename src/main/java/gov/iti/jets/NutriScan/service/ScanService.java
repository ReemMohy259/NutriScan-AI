package gov.iti.jets.NutriScan.service;

import gov.iti.jets.NutriScan.dto.ScanResultResponse;
import gov.iti.jets.NutriScan.dto.ScanSubmitResponse;
import gov.iti.jets.NutriScan.exception.ScanNotFoundException;
import gov.iti.jets.NutriScan.mapper.ScanMapper;
import gov.iti.jets.NutriScan.model.Scan;
import gov.iti.jets.NutriScan.repository.ScanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScanService {

    private final ScanRepository scanRepository;
    private final ScanMapper scanMapper;

    public Scan findEntityById(UUID id) {
        return scanRepository.findById(id)
            .orElseThrow(() -> new ScanNotFoundException("Scan not found with id: " + id));
    }

    public ScanResultResponse findById(UUID id) {
        return scanRepository.findById(id)
            .map(scanMapper::toResultResponse)
            .orElseThrow(() -> new ScanNotFoundException("Scan not found with id: " + id));
    }

    public ScanSubmitResponse create(Scan scan) {
        Scan savedScan = scanRepository.save(scan);
        return scanMapper.toSubmitResponse(savedScan);
    }

    public List<ScanResultResponse> findByUserId(UUID userId) {
        return scanRepository.findByUserIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(scanMapper::toResultResponse)
            .toList();
    }

    public List<ScanResultResponse> findByStatus(String status) {
        return scanRepository.findByStatus(status)
            .stream()
            .map(scanMapper::toResultResponse)
            .toList();
    }

    public void delete(UUID id) {
        scanRepository.deleteById(id);
    }
}
