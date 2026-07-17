package gov.iti.jets.NutriScan.service;

import gov.iti.jets.NutriScan.dto.CurrentUserProfileResponse;
import gov.iti.jets.NutriScan.dto.CurrentUserSummaryResponse;
import gov.iti.jets.NutriScan.dto.UpdateProfileRequest;
import gov.iti.jets.NutriScan.exception.UserNotFoundException;
import gov.iti.jets.NutriScan.model.User;
import gov.iti.jets.NutriScan.model.UserAllergy;
import gov.iti.jets.NutriScan.model.UserDisease;
import gov.iti.jets.NutriScan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

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
}
