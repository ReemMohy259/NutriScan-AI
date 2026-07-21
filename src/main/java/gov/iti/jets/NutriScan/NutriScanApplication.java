package gov.iti.jets.NutriScan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableAsync
public class NutriScanApplication {

    public static void main(String[] args) {
        SpringApplication.run(NutriScanApplication.class, args);
    }

}
