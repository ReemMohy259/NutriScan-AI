package gov.iti.jets.NutriScan.repository;

import gov.iti.jets.NutriScan.model.Scan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ScanRepository extends JpaRepository<Scan, UUID> {

    List<Scan> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<Scan> findByStatus(String status);
}
