package gov.iti.jets.keycloak.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.email.EmailException;
import org.keycloak.email.EmailSenderProvider;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpEmailSenderProvider implements EmailSenderProvider {

    private final String apiKey;
    private final String apiUrl;
    private final HttpClient httpClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public HttpEmailSenderProvider(String apiKey, String apiUrl) {
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public void validate(Map<String, String> config) throws EmailException {
        // Basic sanity check that a "from" address is configured in Realm Settings -> Email.
        // We don't validate SMTP host/port/auth here since this provider bypasses SMTP entirely.
        if (config == null || config.get("from") == null || config.get("from").isBlank()) {
            throw new EmailException("Missing 'from' address in realm email settings");
        }
    }

    @Override
    public void send(Map<String, String> config, String address, String subject,
                     String textBody, String htmlBody) throws EmailException {

        String from = config.getOrDefault("from", "no-reply@yourdomain.com");
        String fromDisplayName = config.get("fromDisplayName");
        String fullFrom = (fromDisplayName != null && !fromDisplayName.isBlank())
                ? fromDisplayName + " <" + from + ">"
                : from;

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("from", fullFrom);

            List<String> to = new ArrayList<>();
            to.add(address);
            payload.put("to", to);

            payload.put("subject", subject);
            if (htmlBody != null && !htmlBody.isBlank()) {
                payload.put("html", htmlBody);
            }
            if (textBody != null && !textBody.isBlank()) {
                payload.put("text", textBody);
            }

            String jsonBody = mapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(15))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new EmailException(
                        "Email API returned HTTP " + response.statusCode() + ": " + response.body()
                );
            }

        } catch (EmailException e) {
            throw e;
        } catch (Exception e) {
            throw new EmailException("Failed to send email via HTTP API", e);
        }
    }

    @Override
    public void close() {
        // no-op — HttpClient needs no explicit cleanup
    }
}