package gov.iti.jets.NutriScan.controller;

import gov.iti.jets.NutriScan.dto.*;
import gov.iti.jets.NutriScan.ratelimit.RateLimit;
import gov.iti.jets.NutriScan.ratelimit.RateLimitKeyType;
import gov.iti.jets.NutriScan.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth/")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @RateLimit(limit = 5, keyType = RateLimitKeyType.IP)
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(@RequestBody @Valid RegisterRequest request) {
        return authService.createUser(request);
    }

    @RateLimit(limit = 3, keyType = RateLimitKeyType.IP)
    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(
        @RequestBody @Valid ResendVerificationRequest request) {

        authService.resendEmail(request);

        var response = new ForgotPasswordResponse("Verification Email Send Successfully");

        return ResponseEntity.ok().body(response);
    }

    @RateLimit(limit = 3, keyType = RateLimitKeyType.IP)
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {

        authService.sendForgotPasswordEmail(request);

        var response = new ForgotPasswordResponse("Password reset email sent");

        return ResponseEntity.ok().body(response);
    }
}