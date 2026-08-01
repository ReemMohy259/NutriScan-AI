package gov.iti.jets.NutriScan.repository;

import gov.iti.jets.NutriScan.model.AccountStatus;
import gov.iti.jets.NutriScan.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("""
        select distinct u
        from User u
        left join fetch u.userAllergies ua
        left join fetch ua.allergy
        left join fetch u.userDiseases ud
        left join fetch ud.disease
        where u.id = :id
        """)
    Optional<User> findByIdWithAllergiesAndDiseases(UUID id);

    Page<User> findAllByAccountStatus(AccountStatus accountStatus, Pageable pageable);

    Page<User> findAllByAccountStatusAndToBeDeletedAtBefore(
        AccountStatus accountStatus,
        Instant now,
        Pageable pageable);
}
