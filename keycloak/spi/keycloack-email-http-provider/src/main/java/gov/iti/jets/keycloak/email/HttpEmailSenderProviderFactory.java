package gov.iti.jets.keycloak.email;

import org.keycloak.Config;
import org.keycloak.email.EmailSenderProvider;
import org.keycloak.email.EmailSenderProviderFactory;
import org.keycloak.models.KeycloakSession;

public class HttpEmailSenderProviderFactory implements EmailSenderProviderFactory {

    public static final String PROVIDER_ID = "resend-http";

    private String apiKey;
    private String apiUrl;

    @Override
    public EmailSenderProvider create(KeycloakSession session) {
        return new HttpEmailSenderProvider(apiKey, apiUrl);
    }

    @Override
    public void init(Config.Scope config) {
        // Populated from env vars: KC_SPI_EMAIL_SENDER_RESEND_HTTP_API_KEY / API_URL
        this.apiKey = config.get("api-key");
        this.apiUrl = config.get("api-url", "https://api.resend.com/emails");

        if (this.apiKey == null || this.apiKey.isBlank()) {
            throw new IllegalStateException(
                    "Missing required SPI config: spi-email-sender-resend-http-api-key"
            );
        }
    }

    @Override
    public void postInit(org.keycloak.models.KeycloakSessionFactory factory) {
        // no-op
    }

    @Override
    public void close() {
        // no-op
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}