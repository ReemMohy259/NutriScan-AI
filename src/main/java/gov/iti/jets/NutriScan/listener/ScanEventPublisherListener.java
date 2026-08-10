package gov.iti.jets.NutriScan.listener;

import gov.iti.jets.NutriScan.dto.ai.ScanStatus;
import gov.iti.jets.NutriScan.listener.event.ScanDeletedEvent;
import gov.iti.jets.NutriScan.listener.event.ScanStatusChangedEvent;
import gov.iti.jets.NutriScan.listener.event.UserDeletedEvent;
import gov.iti.jets.NutriScan.model.ElasticsearchSync;
import gov.iti.jets.NutriScan.model.EntityType;
import gov.iti.jets.NutriScan.model.SyncOperation;
import gov.iti.jets.NutriScan.repository.ElasticsearchSyncRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ScanEventPublisherListener {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.scan.events.exchange.name}")
    private String scanExchange;

    @Value("${rabbitmq.scan.index.routing-key}")
    private String scanIndexRoutingKey;

    @Value("${rabbitmq.scan.delete.routing-key}")
    private String scanDeleteRoutingKey;

    @Value("${rabbitmq.user.delete.routing-key}")
    private String userDeleteRoutingKey;

    private final ElasticsearchSyncRepository syncRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onScanStatusChange(ScanStatusChangedEvent event) {

        if (event.status() == ScanStatus.PROCESSING) {
            return;
        }

        syncRepository.save(
            ElasticsearchSync.builder()
                .id(UUID.randomUUID())
                .entityType(EntityType.SCAN)
                .entityId(event.scanId())
                .operation(SyncOperation.UPSERT)
                .build());

        rabbitTemplate.convertAndSend(scanExchange, scanIndexRoutingKey, event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onScanDeleted(ScanDeletedEvent event) {

        syncRepository.save(
            ElasticsearchSync.builder()
                .entityType(EntityType.SCAN)
                .entityId(event.scanId())
                .operation(SyncOperation.DELETE)
                .build());

        rabbitTemplate.convertAndSend(scanExchange, scanDeleteRoutingKey, event);
    }

    @EventListener
    public void onUserDeleted(UserDeletedEvent event) {

        syncRepository.save(
            ElasticsearchSync.builder()
                .entityType(EntityType.USER)
                .entityId(event.userId())
                .operation(SyncOperation.DELETE)
                .build());

        rabbitTemplate.convertAndSend(scanExchange, userDeleteRoutingKey, event);
    }
}