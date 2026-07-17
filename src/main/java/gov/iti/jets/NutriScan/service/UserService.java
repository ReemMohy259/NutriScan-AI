package gov.iti.jets.NutriScan.service;

import gov.iti.jets.NutriScan.dto.CurrentUserProfileResponse;
import gov.iti.jets.NutriScan.dto.CurrentUserSummaryResponse;
import gov.iti.jets.NutriScan.dto.UpdateProfileRequest;
import gov.iti.jets.NutriScan.exception.UserNotFoundException;
import gov.iti.jets.NutriScan.model.User;
import gov.iti.jets.NutriScan.model.UserAllergy;
import gov.iti.jets.NutriScan.model.UserDisease;
import gov.iti.jets.NutriScan.repository.UserRepository;
import gov.iti.jets.NutriScan.dto.RegisterRequest;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService {

    @Value("${keycloak.realm.name}")
    private String realmName;
    private final UserRepository userRepository;

    public User findById(UUID id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User update(UUID id, User userDetails) {
        User user = findById(id);

        if (userDetails.getDateOfBirth() != null) {
            user.setDateOfBirth(userDetails.getDateOfBirth());
        }

        if (userDetails.getGender() != null) {
            user.setGender(userDetails.getGender());
        }

        if (userDetails.getHeightCm() != null) {
            user.setHeightCm(userDetails.getHeightCm());
        }

    private final Keycloak keycloak;
        if (userDetails.getWeightKg() != null) {
            user.setWeightKg(userDetails.getWeightKg());
        }

    // handle that the username or email already exist
    public void createUser(RegisterRequest request) {
        if (userDetails.getUserAllergies() != null) {
            user.setUserAllergies(userDetails.getUserAllergies());
        }

        UserRepresentation user = new UserRepresentation();
        if (userDetails.getUserDiseases() != null) {
            user.setUserDiseases(userDetails.getUserDiseases());
        }

        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        return userRepository.save(user);
    }

        user.setEnabled(true);
        user.setEmailVerified(false);
        user.setRequiredActions(Collections.emptyList());
    public User addAllergy(UUID userId, UserAllergy allergy) {
        User user = findById(userId);
        user.addAllergy(allergy);
        return userRepository.save(user);
    }

        Response response = keycloak.realm(realmName).users().create(user);
    public User addDisease(UUID userId, UserDisease disease) {
        User user = findById(userId);
        user.addDiseases(disease);
        return userRepository.save(user);
    }

        String userId = CreatedResponseUtil.getCreatedId(response);
    public boolean existsById(UUID id) {
        return userRepository.existsById(id);
    }

        CredentialRepresentation password = new CredentialRepresentation();
    public CurrentUserSummaryResponse getCurrentUserSummary() {
        return null;
    }

        password.setType(CredentialRepresentation.PASSWORD);
        password.setTemporary(false);
        password.setValue(request.password());
    public CurrentUserProfileResponse getCurrentUserProfile() {
        return null;
    }

        keycloak.realm(realmName).users().get(userId).resetPassword(password);
    public CurrentUserProfileResponse updateUserProfile(UpdateProfileRequest request) {

        // Don't forget to check ownership first
        return null;
    }
}
