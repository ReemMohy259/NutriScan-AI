package gov.iti.jets.NutriScan.repository;

import gov.iti.jets.NutriScan.model.Disease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DiseaseRepository extends JpaRepository<Disease, Integer> {

    Optional<Disease> findByName(String name);

    List<Disease> findByNameContainingIgnoreCase(String name);

    boolean existsByName(String name);

    List<Disease> findAllByNameIn(Set<String> names);

    @Modifying
    @Query("DELETE FROM Disease d WHERE d.id = :id")
    int deleteDiseaseById(@Param("id") Integer id);

}
