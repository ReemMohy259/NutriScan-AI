package gov.iti.jets.NutriScan.repository;

import gov.iti.jets.NutriScan.model.Allergy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AllergyRepository extends JpaRepository<Allergy, Integer> {

    Optional<Allergy> findByName(String name);

    boolean existsByName(String name);

    List<Allergy> findAllByNameIn(Set<String> names);

    @Modifying
    @Query("DELETE FROM Allergy a WHERE a.id = :id")
    int deleteAllergyById(@Param("id") Integer id);
}
