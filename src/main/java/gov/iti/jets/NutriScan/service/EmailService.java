package gov.iti.jets.NutriScan.service;

import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Async
    public void sendVerificationEmail(UsersResource usersResource, String userId) {
        usersResource.get(userId).sendVerifyEmail(3600);
    }
}
