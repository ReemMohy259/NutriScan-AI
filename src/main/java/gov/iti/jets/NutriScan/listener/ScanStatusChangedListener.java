package gov.iti.jets.NutriScan.listener;

import gov.iti.jets.NutriScan.listener.event.ScanStatusChangedEvent;
import gov.iti.jets.NutriScan.service.ScanNotificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@AllArgsConstructor
public class ScanStatusChangedListener {

    private final ScanNotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onScanCompleted(ScanStatusChangedEvent event) {

        notificationService.notifyUser(event.userId(), event.scanId(), event.status());
    }
}