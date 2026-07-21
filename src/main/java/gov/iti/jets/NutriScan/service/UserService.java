package gov.iti.jets.NutriScan.service;

import gov.iti.jets.NutriScan.dto.*;
import gov.iti.jets.NutriScan.exception.AllergyNotFoundException;
import gov.iti.jets.NutriScan.exception.DiseaseNotFoundException;
import gov.iti.jets.NutriScan.exception.UserNotFoundException;
import gov.iti.jets.NutriScan.mapper.AllergyMapper;
import gov.iti.jets.NutriScan.mapper.DiseaseMapper;
import gov.iti.jets.NutriScan.mapper.UserMapper;
import gov.iti.jets.NutriScan.model.*;
import gov.iti.jets.NutriScan.repository.AllergyRepository;
import gov.iti.jets.NutriScan.repository.DiseaseRepository;
import gov.iti.jets.NutriScan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    @Value("${keycloak.realm.name}")
    private String realmName;

    private final Keycloak keycloak;

    private final UserRepository userRepository;

    private final AllergyRepository allergyRepository;

    private final DiseaseRepository diseaseRepository;

    private final UserMapper userMapper;

    private final AllergyMapper allergyMapper;

    private final DiseaseMapper diseaseMapper;

    public User findById(UUID id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void update(UUID id, UpdateProfileRequest userDetails) {
        User user = findById(id);

        if (userDetails.dateOfBirth() != null) {
            user.setDateOfBirth(userDetails.dateOfBirth());
        }

        if (userDetails.gender() != null) {
            user.setGender(userDetails.gender());
        }

        if (userDetails.heightCm() != null) {
            user.setHeightCm(userDetails.heightCm());
        }

        if (userDetails.weightKg() != null) {
            user.setWeightKg(userDetails.weightKg());
        }

        if (userDetails.allergyIds() != null && !userDetails.allergyIds().isEmpty()) {
            Set<UserAllergy> newAllergies = buildUserAllergies(id, user, userDetails.allergyIds());
            Set<UserAllergy> oldAllergies = user.getUserAllergies();

            for (UserAllergy allergy : oldAllergies) {
                user.removeAllergy(allergy);
            }

            for (UserAllergy allergy : newAllergies) {
                user.addAllergy(allergy);
            }
        }

        if (userDetails.diseaseIds() != null && !userDetails.diseaseIds().isEmpty()) {
            Set<UserDisease> newDiseases = buildUserDiseases(id, user, userDetails.diseaseIds());
            Set<UserDisease> oldDiseases = user.getUserDiseases();

            for (UserDisease disease : oldDiseases) {
                user.removeDiseases(disease);
            }

            for (UserDisease disease : newDiseases) {
                user.addDiseases(disease);
            }
        }

        userRepository.save(user);
    }

    public User addAllergy(UUID userId, UserAllergy allergy) {
        User user = findById(userId);
        user.addAllergy(allergy);
        return userRepository.save(user);
    }

    public User addDisease(UUID userId, UserDisease disease) {
        User user = findById(userId);
        user.addDiseases(disease);
        return userRepository.save(user);
    }

    public boolean existsById(UUID id) {
        return userRepository.existsById(id);
    }

    public CurrentUserSummaryResponse getCurrentUserSummary(Jwt jwt) {

        CurrentUserProfileResponse user = getFullUserFromToken(jwt);

        return userMapper.toResponse(user);
    }

    public CurrentUserProfileResponse getCurrentUserProfile(Jwt jwt) {

        return getFullUserFromToken(jwt);
    }

    public void updateUserProfile(UpdateProfileRequest request, Jwt jwt) {

        String userId = jwt.getClaim("sub");
        UsersResource usersResource = keycloak.realm(realmName).users();
        UserRepresentation user = usersResource.get(userId).toRepresentation();

        if (request.firstName() != null) {
            user.setFirstName(request.firstName());
        }

        if (request.lastName() != null) {
            user.setLastName(request.lastName());
        }

        usersResource.get(userId).update(user);

        update(UUID.fromString(userId), request);
    }

    private CurrentUserProfileResponse getFullUserFromToken(Jwt jwt) {

        UUID userId = UUID.fromString(jwt.getClaim("sub"));

        UsersResource usersResource = keycloak.realm(realmName).users();
        UserRepresentation userRepresentation = usersResource.get(String.valueOf(userId))
            .toRepresentation();

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        return CurrentUserProfileResponse.builder()
            .id(userId)
            .email(userRepresentation.getEmail())
            .username(userRepresentation.getUsername())
            .firstName(userRepresentation.getFirstName())
            .lastName(userRepresentation.getLastName())
            .dateOfBirth(user.getDateOfBirth())
            .gender(user.getGender())
            .heightCm(user.getHeightCm())
            .weightKg(user.getWeightKg())
            .allergies(
                allergyMapper.toResponseList(
                    user.getUserAllergies().stream().map(UserAllergy::getAllergy).toList()))
            .diseases(
                diseaseMapper.toResponseList(
                    user.getUserDiseases().stream().map(UserDisease::getDisease).toList()))
            .updatedAt(user.getUpdatedAt())
            .build();
    }

    public RegisterResponse register(UUID userId, RegisterRequest request) {

        User user = User.builder()
            .id(userId)
            .gender(request.gender())
            .dateOfBirth(request.dateOfBirth())
            .heightCm(request.heightCm())
            .weightKg(request.weightKg())
            .build();

        user.setUserAllergies(buildUserAllergies(userId, user, request.allergies()));
        user.setUserDiseases(buildUserDiseases(userId, user, request.diseases()));

        userRepository.save(user);

        return new RegisterResponse("Registration successful", true);
    }

    // Helper Methods
    private Set<UserAllergy> buildUserAllergies(UUID userId, User user, List<Integer> allergyIds) {
        return allergyIds.stream().map(id -> {
            Allergy allergy = allergyRepository.findById(id)
                .orElseThrow(
                    () -> new AllergyNotFoundException("Allergy not found with ID: " + id));

            UserAllergy ua = new UserAllergy();
            ua.setId(new UserAllergyId(userId, id));
            ua.setUser(user);
            ua.setAllergy(allergy);

            return ua;
        }).collect(Collectors.toSet());
    }

    private Set<UserDisease> buildUserDiseases(UUID userId, User user, List<Integer> diseaseIds) {
        return diseaseIds.stream().map(id -> {
            Disease disease = diseaseRepository.findById(id)
                .orElseThrow(
                    () -> new DiseaseNotFoundException("Disease not found with ID: " + id));

            UserDisease ud = new UserDisease();
            ud.setId(new UserDiseaseId(userId, id));
            ud.setUser(user);
            ud.setDisease(disease);

            return ud;
        }).collect(Collectors.toSet());
    }
}
