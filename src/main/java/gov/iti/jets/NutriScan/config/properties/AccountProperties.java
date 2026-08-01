package gov.iti.jets.NutriScan.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nutriscan.account")
public record AccountProperties(int deletionGracePeriodDays) {
}