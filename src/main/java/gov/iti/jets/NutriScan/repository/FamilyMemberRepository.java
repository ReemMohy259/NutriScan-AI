package gov.iti.jets.NutriScan.repository;

import gov.iti.jets.NutriScan.model.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FamilyMemberRepository extends JpaRepository<FamilyMember, UUID> {

}
