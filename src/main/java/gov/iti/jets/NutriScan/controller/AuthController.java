package gov.iti.jets.NutriScan.controller;

import gov.iti.jets.NutriScan.dto.ForgotPasswordRequest;
import gov.iti.jets.NutriScan.dto.RegisterRequest;
import gov.iti.jets.NutriScan.dto.RegisterResponse;
import gov.iti.jets.NutriScan.dto.ResendVerificationRequest;
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

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(@RequestBody @Valid RegisterRequest request) {
        return authService.createUser(request);
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Void> resendVerification(
        @RequestBody @Valid ResendVerificationRequest request) {

        authService.resendEmail(request);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody @Valid ForgotPasswordRequest request) {

        var response = authService.sendForgotPasswordEmail(request);

        return ResponseEntity.ok().body(response);
    }
}