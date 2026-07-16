package gov.iti.jets.NutriScan.service;

import gov.iti.jets.NutriScan.dto.RegisterRequest;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService {

    private final Keycloak keycloak;

    // handle that the username or email already exist
    public void createUser(RegisterRequest request) {

        UserRepresentation user = new UserRepresentation();

        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());

        user.setEnabled(true);
        user.setEmailVerified(false);
        user.setRequiredActions(Collections.emptyList());

        Response response =
                keycloak.realm("nutriscan")
                        .users()
                        .create(user);

        String userId = CreatedResponseUtil.getCreatedId(response);

        CredentialRepresentation password = new CredentialRepresentation();

        password.setType(CredentialRepresentation.PASSWORD);
        password.setTemporary(false);
        password.setValue(request.password());

        keycloak.realm("nutriscan")
                .users()
                .get(userId)
                .resetPassword(password);
    }
}