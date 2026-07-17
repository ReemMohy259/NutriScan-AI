package gov.iti.jets.NutriScan.controller;

import gov.iti.jets.NutriScan.dto.CurrentUserProfileResponse;
import gov.iti.jets.NutriScan.dto.CurrentUserSummaryResponse;
import gov.iti.jets.NutriScan.dto.UpdateProfileRequest;
import gov.iti.jets.NutriScan.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    UserService userService;

    @GetMapping("/me")
    public CurrentUserSummaryResponse getCurrentUserSummary() {
        return userService.getCurrentUserSummary();
    }

    @GetMapping("/profile")
    public CurrentUserProfileResponse getCurrentUserProfile() {
        return userService.getCurrentUserProfile();
    }

    @PatchMapping("/profile")
    public CurrentUserProfileResponse updateUserProfile(UpdateProfileRequest request) {
        return userService.updateUserProfile(request);
    }
}
