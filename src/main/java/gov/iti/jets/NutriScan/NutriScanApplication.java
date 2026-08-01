package gov.iti.jets.NutriScan;

import gov.iti.jets.NutriScan.config.properties.AccountProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties({AccountProperties.class})
@EnableScheduling
public class NutriScanApplication {

    public static void main(String[] args) {
        SpringApplication.run(NutriScanApplication.class, args);
    }

}
