package gov.iti.jets.NutriScan.scheduler;

import gov.iti.jets.NutriScan.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserReconciliationScheduler {

    private final UserService userService;

    @Scheduled(cron = "0 30 3 * * *")
    public void reconcileUsers() {
        log.info("Started reconciliation...");
        long startTime = System.currentTimeMillis();
        userService.reconcileAccounts(userService);
        log.info("Finished in {} ms", System.currentTimeMillis() - startTime);
    }
}