package gov.iti.jets.NutriScan.service;

import gov.iti.jets.NutriScan.dto.*;
import gov.iti.jets.NutriScan.exception.AllergyNotFoundException;
import gov.iti.jets.NutriScan.exception.DiseaseNotFoundException;
import gov.iti.jets.NutriScan.exception.UserNotFoundException;
import gov.iti.jets.NutriScan.model.*;
import gov.iti.jets.NutriScan.repository.AllergyRepository;
import gov.iti.jets.NutriScan.repository.DiseaseRepository;
import gov.iti.jets.NutriScan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final AllergyRepository allergyRepository;

    private final DiseaseRepository diseaseRepository;

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

        if (userDetails.getWeightKg() != null) {
            user.setWeightKg(userDetails.getWeightKg());
        }

        if (userDetails.getUserAllergies() != null) {
            user.setUserAllergies(userDetails.getUserAllergies());
        }

        if (userDetails.getUserDiseases() != null) {
            user.setUserDiseases(userDetails.getUserDiseases());
        }

        return userRepository.save(user);
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

    public CurrentUserSummaryResponse getCurrentUserSummary() {
        return null;
    }

    public CurrentUserProfileResponse getCurrentUserProfile() {
        return null;
    }

    public CurrentUserProfileResponse updateUserProfile(UpdateProfileRequest request) {

        // Don't forget to check ownership first
        return null;
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
