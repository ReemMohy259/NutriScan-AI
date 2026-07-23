package gov.iti.jets.NutriScan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class HttpConfig {

    @Bean
    RestClient tavilyClient(RestClient.Builder builder) {
        return builder.build();
    }

}