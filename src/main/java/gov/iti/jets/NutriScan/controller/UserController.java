package gov.iti.jets.NutriScan.controller;

import gov.iti.jets.NutriScan.dto.CurrentUserProfileResponse;
import gov.iti.jets.NutriScan.dto.CurrentUserSummaryResponse;
import gov.iti.jets.NutriScan.dto.UpdateProfileRequest;
import gov.iti.jets.NutriScan.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public CurrentUserSummaryResponse getCurrentUserSummary(@AuthenticationPrincipal Jwt jwt) {
        return userService.getCurrentUserSummary(jwt);
    }

    @GetMapping("/profile")
    public CurrentUserProfileResponse getCurrentUserProfile(@AuthenticationPrincipal Jwt jwt) {
        return userService.getCurrentUserProfile(jwt);
    }

    @PatchMapping("/profile")
    public CurrentUserProfileResponse updateUserProfile(
        @AuthenticationPrincipal Jwt jwt,
        @RequestBody UpdateProfileRequest request) {
        userService.updateUserProfile(request, jwt);

        return userService.getCurrentUserProfile(jwt);
    }

    @PostMapping("/profile/image")
    public CurrentUserProfileResponse uploadUserProfilePicture(
        @AuthenticationPrincipal Jwt jwt,
        @RequestParam("image") MultipartFile image) {
        return userService.uploadUserProfileImage(image, jwt);
    }
}
