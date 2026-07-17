package gov.iti.jets.NutriScan.repository;

import gov.iti.jets.NutriScan.model.Allergy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AllergyRepository extends JpaRepository<Allergy, Integer> {

    Optional<Allergy> findByName(String name);

    boolean existsByName(String name);
}
