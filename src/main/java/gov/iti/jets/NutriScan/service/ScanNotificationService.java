package gov.iti.jets.NutriScan.service;

import gov.iti.jets.NutriScan.dto.ScanNotification;
import gov.iti.jets.NutriScan.dto.ai.ScanStatus;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ScanNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyUser(UUID userId, UUID scanId, ScanStatus status) {

        messagingTemplate.convertAndSendToUser(
            userId.toString(),
            "/queue/scans",
            new ScanNotification(scanId, status));
    }

}