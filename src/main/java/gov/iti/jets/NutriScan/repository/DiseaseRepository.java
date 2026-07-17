package gov.iti.jets.NutriScan.repository;

import gov.iti.jets.NutriScan.model.Disease;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiseaseRepository extends JpaRepository<Disease, Integer> {

    Optional<Disease> findByName(String name);

    List<Disease> findByNameContainingIgnoreCase(String name);

}
