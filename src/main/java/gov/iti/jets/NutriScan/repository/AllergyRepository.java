package gov.iti.jets.NutriScan.repository;

import gov.iti.jets.NutriScan.exception.AllergyNotFoundException;
import gov.iti.jets.NutriScan.model.Allergy;
import gov.iti.jets.NutriScan.model.UserAllergy;
import gov.iti.jets.NutriScan.model.UserAllergyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public interface AllergyRepository extends JpaRepository<Allergy, Integer> {

    Optional<Allergy> findByName(String name);

    boolean existsByName(String name);

    List<Allergy> findAllByNameIn(Set<String> names);

    @Modifying
    @Query("DELETE FROM Allergy a WHERE a.id = :id")
    int deleteAllergyById(@Param("id") Integer id);

//    allergyIds.stream().map(id -> {
//        Allergy allergy = allergyRepository.findById(id)
//                .orElseThrow(
//                        () -> new AllergyNotFoundException("Allergy not found with ID: " + id));
//
//        UserAllergy ua = new UserAllergy();
//        ua.setId(new UserAllergyId(userId, id));
//        ua.setUser(user);
//        ua.setAllergy(allergy);
//
//        return ua;
//    }).collect(Collectors.toSet());
    Set<Allergy> findAllByIdIn(List<Integer> ids);

}
