package gov.iti.jets.NutriScan.listener;

import gov.iti.jets.NutriScan.exception.ScanNotFoundException;
import gov.iti.jets.NutriScan.listener.event.ScanDeletedEvent;
import gov.iti.jets.NutriScan.listener.event.ScanStatusChangedEvent;
import gov.iti.jets.NutriScan.listener.event.UserDeletedEvent;
import gov.iti.jets.NutriScan.mapper.ScanMapper;
import gov.iti.jets.NutriScan.model.Scan;
import gov.iti.jets.NutriScan.model.elasticsearch.ScanDocument;
import gov.iti.jets.NutriScan.repository.ScanRepository;
import gov.iti.jets.NutriScan.repository.elasticsearch.ScanSearchRepository;
import gov.iti.jets.NutriScan.service.ScanSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ScanSearchSyncListener {

    private final ScanRepository scanRepository;
    private final ScanSearchRepository scanSearchRepository;
    private final ScanSearchService scanSearchService;
    private final ScanMapper scanMapper;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.scan.index.retry.routing-key}")
    private String retryRoutingKey;

    @Value("${rabbitmq.scan.index.dlq.routing-key}")
    private String dlqRoutingKey;

    @Value("${rabbitmq.scan.events.exchange.name}")
    private String scanExchange;

    @Value("${rabbitmq.scan.events.dlx.name}")
    private String scanDlxExchange;

    private static final int MAX_RETRIES = 3;
    private static final String RETRY_HEADER = "x-retry-count";

    @RabbitListener(queues = "${rabbitmq.scan.index.queue.name}")
    public void insertScanDocumentToElasticSearch(ScanStatusChangedEvent event, Message message) {

        try {
            Scan scan = scanRepository.findById(event.scanId())
                .orElseThrow(() -> new ScanNotFoundException("Scan not found: " + event.scanId()));

            ScanDocument document = scanMapper.toDocument(scan);

            scanSearchRepository.save(document);

            log.info("Indexed scan {}", event.scanId());
        } catch (Exception ex) {

            int retries = getRetryCount(message);

            log.warn(
                "Failed indexing scan {} (attempt {}/{})",
                event.scanId(),
                retries,
                MAX_RETRIES,
                ex);

            if (retries >= MAX_RETRIES) {

                rabbitTemplate.convertAndSend(scanDlxExchange, dlqRoutingKey, event, m -> {
                    m.getMessageProperties().setHeader(RETRY_HEADER, retries);
                    return m;
                });

                log.error("Scan {} moved to DLQ after {} retries", event.scanId(), retries);

                return;
            }

            rabbitTemplate.convertAndSend(scanExchange, retryRoutingKey, event, m -> {
                m.getMessageProperties().setHeader(RETRY_HEADER, retries + 1);
                return m;
            });
        }
    }

    @RabbitListener(queues = "${rabbitmq.scan.delete.queue.name}")
    public void deleteScanDocument(ScanDeletedEvent event) {

        scanSearchRepository.deleteById(event.scanId());
    }

    @RabbitListener(queues = "${rabbitmq.user.delete.queue.name}")
    public void deleteUser(UserDeletedEvent event) {

        scanSearchService.deleteAllByUserId(event.userId());
    }

    @RabbitListener(queues = "${rabbitmq.scan.index.dlq.name}")
    public void logIndexDlqEvents(ScanStatusChangedEvent event) {

        log.warn("""
            Failed Index From Dlq:
             UserId: {}
             ScanId: {}
             ScanStatus: {}
            """, event.userId(), event.scanId(), event.status());
    }

    @RabbitListener(queues = "${rabbitmq.scan.delete.dlq.name}")
    public void logDeleteScanDlqEvents(ScanDeletedEvent event) {

        log.warn("""
            Failed Delete Document:
             ScanId: {}
            """, event.scanId());
    }

    @RabbitListener(queues = "${rabbitmq.user.delete.dlq.name}")
    public void logDeleteUserDlqEvents(UserDeletedEvent event) {

        log.warn("""
            Failed Delete Indexes When user is deleted:
             UserId: {}
            """, event.userId());
    }

    private int getRetryCount(Message message) {

        Object value = message.getMessageProperties().getHeaders().get(RETRY_HEADER);

        if (value instanceof Number number) {
            return number.intValue();
        }

        return 0;
    }
}