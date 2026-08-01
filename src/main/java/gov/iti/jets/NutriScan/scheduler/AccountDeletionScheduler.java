package gov.iti.jets.NutriScan.scheduler;

import gov.iti.jets.NutriScan.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountDeletionScheduler {

    private final UserService userService;

    @Scheduled(cron = "0 0 2 * * *")
    public void deleteAccounts() {
        log.info("Started Account Clean up...");
        long startTime = System.currentTimeMillis();
        userService.deleteExpiredAccounts(userService);
        log.info("Finished in {} ms", System.currentTimeMillis() - startTime);
    }
}