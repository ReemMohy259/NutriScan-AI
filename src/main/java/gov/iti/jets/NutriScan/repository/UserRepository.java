package gov.iti.jets.NutriScan.repository;

import gov.iti.jets.NutriScan.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

    @Modifying
    @Transactional
    @Query("""
            UPDATE User u
            SET
                u.dailyStreak = CASE
                    WHEN u.lastActiveDate = :today THEN u.dailyStreak
                    WHEN u.lastActiveDate = :yesterday THEN u.dailyStreak + 1
                    ELSE 1
                END,
                u.lastActiveDate = :today
            WHERE u.id = :userId AND (u.lastActiveDate IS NULL OR u.lastActiveDate <> :today)
        """)
    int updateDailyStreak(
        @Param("userId") UUID userId,
        @Param("today") LocalDate today,
        @Param("yesterday") LocalDate yesterday);

}
