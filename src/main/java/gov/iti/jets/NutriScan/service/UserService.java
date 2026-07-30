package gov.iti.jets.NutriScan.service;

import gov.iti.jets.NutriScan.dto.*;
import gov.iti.jets.NutriScan.exception.*;
import gov.iti.jets.NutriScan.mapper.AllergyMapper;
import gov.iti.jets.NutriScan.mapper.DiseaseMapper;
import gov.iti.jets.NutriScan.mapper.FamilyMemberMapper;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
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

    private final FamilyMemberMapper familyMemberMapper;

    private final CloudinaryStorageService cloudinaryStorageService;

    public User findById(UUID id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    @Transactional
    public void updateUserProfile(UpdateProfileRequest request, Jwt jwt) {

        String userId = jwt.getClaim("sub");
        boolean isFirstNameUpdated = request.firstName() != null && !request.firstName().isBlank();
        boolean isLastNameUpdated = request.lastName() != null && !request.lastName().isBlank();

        if (isFirstNameUpdated || isLastNameUpdated) {
            UsersResource usersResource = keycloak.realm(realmName).users();
            UserRepresentation user = usersResource.get(userId).toRepresentation();

            if (isFirstNameUpdated) {
                user.setFirstName(request.firstName());
            }

            if (isLastNameUpdated) {
                user.setLastName(request.lastName());
            }

            usersResource.get(userId).update(user);
        }

        update(UUID.fromString(userId), request);
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

        updateUserAllergies(user, userDetails.allergyIds());

        updateUserDiseases(user, userDetails.diseaseIds());

        updateFamilyMembers(user, userDetails.familyMembers());
    }

    private void updateFamilyMembers(User user, List<FamilyMemberRequest> requests) {

        if (requests == null) {
            return;
        }

        user.getFamilyMembers().clear();

        if (!requests.isEmpty()) {
            buildFamilyMembers(user, requests).forEach(user::addFamilyMember);
        }

        userRepository.flush();
    }

    private void updateUserAllergies(User user, List<Integer> allergyIds) {

        if (allergyIds == null) {
            return;
        }

        user.getUserAllergies().clear();
        userRepository.flush();

        if (!allergyIds.isEmpty()) {
            buildUserAllergies(user.getId(), user, allergyIds).forEach(user::addAllergy);
        }

        userRepository.flush();
    }

    private void updateUserDiseases(User user, List<Integer> diseaseIds) {

        if (diseaseIds == null) {
            return;
        }

        user.getUserDiseases().clear();
        userRepository.flush();

        if (!diseaseIds.isEmpty()) {
            buildUserDiseases(user.getId(), user, diseaseIds).forEach(user::addDiseases);
        }

        userRepository.flush();
    }

    @Transactional
    public CurrentUserProfileResponse uploadUserProfileImage(MultipartFile image, Jwt jwt) {

        final long MAX_IMAGE_SIZE_BYTES = 5 * 1024 * 1024;

        if (image == null || image.isEmpty())
            throw new NoImageProvidedException("Image is required");

        String contentType = image.getContentType();

        if (contentType == null || !contentType.startsWith("image/"))
            throw new InvalidImageException("Only image files are allowed");

        if (image.getSize() > MAX_IMAGE_SIZE_BYTES)
            throw new ImageTooLargeException("Image size must not exceed 5 MB");

        UUID userId = UUID.fromString(jwt.getClaim("sub"));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        try {
            String url = cloudinaryStorageService.upload(image);
            user.setImageUrl(url);
        } catch (IOException e) {
            throw new ImageUploadException("Failed to upload image");
        }

        userRepository.save(user);

        return getFullUser(jwt);
    }

    public CurrentUserSummaryResponse getCurrentUserSummary(Jwt jwt) {

        CurrentUserProfileResponse user = getFullUser(jwt);

        return userMapper.toResponse(user);
    }

    public CurrentUserProfileResponse getCurrentUserProfile(Jwt jwt) {

        return getFullUser(jwt);
    }

    private CurrentUserProfileResponse getFullUser(Jwt jwt) {

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
            .imageUrl(user.getImageUrl())
            .dateOfBirth(user.getDateOfBirth())
            .gender(user.getGender())
            .heightCm(user.getHeightCm())
            .weightKg(user.getWeightKg())
            .bmi(calculateBmi(user.getHeightCm(), user.getWeightKg()))
            .tdee(
                calculateTdee(
                    user.getDateOfBirth(),
                    user.getHeightCm(),
                    user.getWeightKg(),
                    user.getGender()))
            .allergies(
                allergyMapper.toResponseList(
                    user.getUserAllergies().stream().map(UserAllergy::getAllergy).toList()))
            .diseases(
                diseaseMapper.toResponseList(
                    user.getUserDiseases().stream().map(UserDisease::getDisease).toList()))
            .familyMembers(
                familyMemberMapper.toResponseList(user.getFamilyMembers().stream().toList()))
            .updatedAt(user.getUpdatedAt())
            .dailyStreak(user.getDailyStreak())
            .build();
    }

    @Transactional
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
        user.setFamilyMembers(buildFamilyMembers(user, request.familyMembers()));

        userRepository.save(user);

        return new RegisterResponse("Registration successful", true);
    }

    public UserAllergiesAndConditionsResponse getUserAllergiesAndConditions(UUID userId) {
        User user = userRepository.findByIdWithAllergiesAndDiseases(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<String> allergies = user.getUserAllergies()
            .stream()
            .map(userAllergy -> userAllergy.getAllergy().getName())
            .toList();

        List<String> diseases = user.getUserDiseases()
            .stream()
            .map(userDisease -> userDisease.getDisease().getName())
            .toList();

        return new UserAllergiesAndConditionsResponse(allergies, diseases);
    }

    public void checkDailyStreak(Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getClaim("sub"));
        LocalDate today = LocalDate.now();

        userRepository.updateDailyStreak(userId, today, today.minusDays(1));
    }
    // Helper Methods
    private Set<UserAllergy> buildUserAllergies(UUID userId, User user, List<Integer> allergyIds) {
        Set<Allergy> allergies = allergyRepository.findAllByIdIn(allergyIds);

        Set<Integer> foundIds = allergies.stream().map(Allergy::getId).collect(Collectors.toSet());

        String notFoundIds = allergyIds.stream()
            .filter(id -> !foundIds.contains(id))
            .map(String::valueOf)
            .collect(Collectors.joining(", "));

        if (!notFoundIds.isEmpty()) {
            throw new AllergyNotFoundException("Allergy not found with ids: " + notFoundIds);
        }

        // TODO: fix multiple select and insert statments (maybe batch them)
        return allergies.stream().map(allergy -> {

            UserAllergy userAllergy = new UserAllergy();
            userAllergy.setId(new UserAllergyId(userId, allergy.getId()));
            userAllergy.setUser(user);
            userAllergy.setAllergy(allergy);

            return userAllergy;
        }).collect(Collectors.toSet());
    }

    private Set<UserDisease> buildUserDiseases(UUID userId, User user, List<Integer> diseaseIds) {
        Set<Disease> diseases = diseaseRepository.findAllByIdIn(diseaseIds);

        Set<Integer> foundIds = diseases.stream().map(Disease::getId).collect(Collectors.toSet());

        String notFoundIds = diseaseIds.stream()
            .filter(id -> !foundIds.contains(id))
            .map(String::valueOf)
            .collect(Collectors.joining(", "));

        if (!notFoundIds.isEmpty()) {
            throw new DiseaseNotFoundException("Disease not found with ids: " + notFoundIds);
        }

        // TODO: fix multiple select and insert statments (maybe batch them)
        return diseases.stream().map(disease -> {

            UserDisease userDisease = new UserDisease();
            userDisease.setId(new UserDiseaseId(userId, disease.getId()));
            userDisease.setUser(user);
            userDisease.setDisease(disease);

            return userDisease;
        }).collect(Collectors.toSet());
    }

    private Set<FamilyMember> buildFamilyMembers(
        User user,
        List<FamilyMemberRequest> familyMemberRequests) {

        if (familyMemberRequests == null || familyMemberRequests.isEmpty()) {
            return new HashSet<>();
        }

        Set<Integer> allergyIds = familyMemberRequests.stream()
            .filter(r -> r.allergyIds() != null)
            .flatMap(r -> r.allergyIds().stream())
            .collect(Collectors.toSet());

        Set<Integer> diseaseIds = familyMemberRequests.stream()
            .filter(r -> r.diseaseIds() != null)
            .flatMap(r -> r.diseaseIds().stream())
            .collect(Collectors.toSet());

        Map<Integer, Allergy> allergiesById = allergyRepository.findAllByIdIn(allergyIds)
            .stream()
            .collect(Collectors.toMap(Allergy::getId, Function.identity()));

        Map<Integer, Disease> diseasesById = diseaseRepository.findAllByIdIn(diseaseIds)
            .stream()
            .collect(Collectors.toMap(Disease::getId, Function.identity()));

        Set<Integer> missingAllergyIds = new HashSet<>(allergyIds);
        missingAllergyIds.removeAll(allergiesById.keySet());

        if (!missingAllergyIds.isEmpty()) {
            throw new AllergyNotFoundException(
                "Allergy not found with ids: " + missingAllergyIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", ")));
        }

        Set<Integer> missingDiseaseIds = new HashSet<>(diseaseIds);
        missingDiseaseIds.removeAll(diseasesById.keySet());

        if (!missingDiseaseIds.isEmpty()) {
            throw new DiseaseNotFoundException(
                "Disease not found with ids: " + missingDiseaseIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", ")));
        }

        Set<FamilyMember> familyMembers = new HashSet<>();

        for (FamilyMemberRequest memberRequest : familyMemberRequests) {

            UUID familyMemberId = UUID.randomUUID();

            FamilyMember familyMember = FamilyMember.builder()
                .id(familyMemberId)
                .name(memberRequest.name())
                .relation(memberRequest.relation())
                .user(user)
                .allergies(new HashSet<>())
                .diseases(new HashSet<>())
                .build();

            if (memberRequest.allergyIds() != null) {
                for (Integer allergyId : memberRequest.allergyIds()) {

                    FamilyMemberAllergy familyMemberAllergy = FamilyMemberAllergy.builder()
                        .id(new FamilyMemberAllergyId(familyMemberId, allergyId))
                        .familyMember(familyMember)
                        .allergy(allergiesById.get(allergyId))
                        .build();

                    familyMember.getAllergies().add(familyMemberAllergy);
                }
            }

            if (memberRequest.diseaseIds() != null) {
                for (Integer diseaseId : memberRequest.diseaseIds()) {

                    FamilyMemberDisease familyMemberDisease = FamilyMemberDisease.builder()
                        .id(new FamilyMemberDiseaseId(familyMemberId, diseaseId))
                        .familyMember(familyMember)
                        .disease(diseasesById.get(diseaseId))
                        .build();

                    familyMember.getDiseases().add(familyMemberDisease);
                }
            }

            familyMembers.add(familyMember);
        }

        return familyMembers;
    }

    private Double calculateBmi(BigDecimal heightInCm, BigDecimal weightInKg) {

        if (heightInCm == null || weightInKg == null) {
            return null;
        }

        return weightInKg.doubleValue() / Math.pow(heightInCm.doubleValue() / 100, 2);
    }

    private Double calculateTdee(
        LocalDate userDob,
        BigDecimal heightInCm,
        BigDecimal weightInKg,
        Gender userGender) {

        if (heightInCm == null || weightInKg == null || userDob == null || userGender == null) {
            return null;
        }

        long age = ChronoUnit.YEARS.between(userDob, LocalDate.now());

        double bmr = (10 * weightInKg.doubleValue()) + (6.25 * heightInCm.doubleValue()) - (5 * age)
            + (userGender.equals(Gender.MALE) ? 5 : -161);

        return bmr * 1.2; // 1.2 resembles (Desk job, little to no intentional exercise).
    }
}
