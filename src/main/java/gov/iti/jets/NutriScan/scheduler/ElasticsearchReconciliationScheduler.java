package gov.iti.jets.NutriScan.scheduler;

import gov.iti.jets.NutriScan.mapper.ScanMapper;
import gov.iti.jets.NutriScan.model.ElasticsearchSync;
import gov.iti.jets.NutriScan.model.Scan;
import gov.iti.jets.NutriScan.repository.ElasticsearchSyncRepository;
import gov.iti.jets.NutriScan.repository.ScanRepository;
import gov.iti.jets.NutriScan.repository.elasticsearch.ScanSearchRepository;
import gov.iti.jets.NutriScan.service.ScanSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ElasticsearchReconciliationScheduler {

    private final ElasticsearchSyncRepository syncRepository;

    private final ScanRepository scanRepository;

    private final ScanSearchRepository searchRepository;

    private final ScanMapper mapper;

    private final ScanSearchService scanSearchService;

    @Scheduled(fixedDelay = 60_00_00)
    @Transactional
    public void reconcileNonIndexedScans() {

        List<ElasticsearchSync> rows = syncRepository
            .findTop500ByProcessedFalseOrderByCreatedAtAsc();

        for (ElasticsearchSync row : rows) {

            try {

                switch (row.getOperation()) {

                    case UPSERT -> upsert(row);

                    case DELETE -> delete(row);
                }

                row.setProcessed(true);

                row.setProcessedAt(Instant.now());

            } catch (Exception ex) {
                row.setRetryCount(row.getRetryCount() + 1);

                row.setLastError(ex.getMessage());
            }
        }
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanup() {

        syncRepository.deleteProcessedOlderThan(Instant.now().minus(30, ChronoUnit.DAYS));
    }

    private void upsert(ElasticsearchSync row) {

        Scan scan = scanRepository.findById(row.getEntityId()).orElse(null);

        if (scan == null) {
            return;
        }

        searchRepository.save(mapper.toDocument(scan));
    }

    private void delete(ElasticsearchSync row) {

        switch (row.getEntityType()) {

            case USER -> scanSearchService.deleteAllByUserId(row.getEntityId());

            case SCAN -> searchRepository.deleteById(row.getEntityId());
        }
    }
}