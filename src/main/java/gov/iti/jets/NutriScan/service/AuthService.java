package gov.iti.jets.NutriScan.service;

import gov.iti.jets.NutriScan.dto.*;
import gov.iti.jets.NutriScan.exception.UserAlreadyVerifiedException;
import gov.iti.jets.NutriScan.exception.UserConflictException;
import gov.iti.jets.NutriScan.exception.UserNotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.hibernate.sql.results.graph.embeddable.EmbeddableInitializer;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    @Value("${keycloak.realm.name}")
    private String realmName;

    private final Keycloak keycloak;

    private final UserService userService;

    private final EmailService emailService;

    public RegisterResponse createUser(RegisterRequest request) {

        UsersResource usersResource = keycloak.realm(realmName).users();
        // Create the user from keycloak
        UserRepresentation user = new UserRepresentation();

        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());

        user.setEnabled(true);
        user.setEmailVerified(false);

        Response response = keycloak.realm(realmName).users().create(user);

        if (response.getStatus() == HttpStatus.SC_CONFLICT) {
            throw new UserConflictException("Username or email already exists.");
        }

        String userId = CreatedResponseUtil.getCreatedId(response);

        try {
            CredentialRepresentation password = new CredentialRepresentation();
            password.setType(CredentialRepresentation.PASSWORD);
            password.setTemporary(false);
            password.setValue(request.password());

            usersResource.get(userId).resetPassword(password);

            // Save the new user in the database
            RegisterResponse registerResponse = userService
                .register(UUID.fromString(userId), request);

            // Send verification email
            try {
                emailService.sendVerificationEmail(usersResource, userId);
            } catch (Exception ex) {
                log.error("Failed to send verification email for user {}", userId);
            }

            return registerResponse;

        } catch (Exception ex) {
            // Roll back Keycloak user
            usersResource.delete(userId);

            throw ex;
        }
    }

    public void resendEmail(ResendVerificationRequest request) {
        UsersResource users = keycloak.realm(realmName).users();

        List<UserRepresentation> foundUsers = users.searchByEmail(request.email(), true);

        if (foundUsers.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }

        UserRepresentation user = foundUsers.get(0);

        if (user.isEmailVerified()) {
            throw new UserAlreadyVerifiedException("User already verified");
        }

        emailService.sendVerificationEmail(users, user.getId());
    }

    public void sendForgotPasswordEmail(ForgotPasswordRequest request) {
        UsersResource users = keycloak.realm(realmName).users();

        List<UserRepresentation> usersFound = users.searchByEmail(request.email(), true);

        if (usersFound.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }

        UserRepresentation user = usersFound.get(0);

        emailService.sendForgotPasswordEmail(users, user.getId());
    }
}
