package gov.iti.jets.NutriScan.controller;

import gov.iti.jets.NutriScan.dto.*;
import gov.iti.jets.NutriScan.ratelimit.RateLimit;
import gov.iti.jets.NutriScan.ratelimit.RateLimitKeyType;
import gov.iti.jets.NutriScan.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @RateLimit(limit = 60)
    @GetMapping("/me")
    public CurrentUserSummaryResponse getCurrentUserSummary(@AuthenticationPrincipal Jwt jwt) {
        return userService.getCurrentUserSummary(jwt);
    }

    @RateLimit(limit = 60)
    @GetMapping("/profile")
    public CurrentUserProfileResponse getCurrentUserProfile(@AuthenticationPrincipal Jwt jwt) {
        return userService.getCurrentUserProfile(jwt);
    }

    @RateLimit(limit = 30)
    @PatchMapping("/profile")
    public CurrentUserProfileResponse updateUserProfile(
        @AuthenticationPrincipal Jwt jwt,
        @RequestBody @Valid UpdateProfileRequest request) {
        userService.updateUserProfile(request, jwt);

        return userService.getCurrentUserProfile(jwt);
    }

    @RateLimit(limit = 5)
    @PostMapping(path = "/profile/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CurrentUserProfileResponse uploadUserProfilePicture(
        @AuthenticationPrincipal Jwt jwt,
        @RequestParam("image") MultipartFile image) {
        return userService.uploadUserProfileImage(image, jwt);
    }

    @RateLimit(limit = 10)
    @PatchMapping(path = "/family-member/{familyMemberId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FamilyMemberResponse uploadUserFamilyMemberPicture(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID familyMemberId,
        @RequestParam("image") MultipartFile image) {
        return userService.uploadUserFamilyMemberImage(familyMemberId, image, jwt);
    }
    @RateLimit(limit = 50)
    @PostMapping("/me/daily-streak")
    public ResponseEntity<Void> updateDailyStreak(@AuthenticationPrincipal Jwt jwt) {
        userService.checkDailyStreak(jwt);
        return ResponseEntity.noContent().build();
    }

    @RateLimit(limit = 3)
    @DeleteMapping("/profile")
    public DeleteAccountResponse deleteUser(@AuthenticationPrincipal Jwt jwt) {

        return userService.scheduleUserForDeletion(jwt);
    }

    @RateLimit(limit = 3)
    @PostMapping("/profile/restore")
    public RestoreAccountResponse restoreUser(@AuthenticationPrincipal Jwt jwt) {

        return userService.restoreAccount(jwt);
    }
}
