package gov.iti.jets.NutriScan.repository;

import gov.iti.jets.NutriScan.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

}
