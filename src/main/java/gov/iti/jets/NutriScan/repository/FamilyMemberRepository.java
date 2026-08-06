package gov.iti.jets.NutriScan.repository;

import gov.iti.jets.NutriScan.model.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface FamilyMemberRepository extends JpaRepository<FamilyMember, UUID> {

    @Query("""
            select distinct fm
            from FamilyMember fm
            left join fetch fm.allergies fa
            left join fetch fa.allergy
            left join fetch fm.diseases fd
            left join fetch fd.disease
            where fm.id = :id
              and fm.user.id = :userId
        """)
    Optional<FamilyMember> findByIdWAndUserIdWithDetails(UUID id, UUID userId);
}
