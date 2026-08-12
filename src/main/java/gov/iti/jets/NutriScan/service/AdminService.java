package gov.iti.jets.NutriScan.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import gov.iti.jets.NutriScan.mapper.ScanMapper;
import gov.iti.jets.NutriScan.model.Scan;
import gov.iti.jets.NutriScan.model.elasticsearch.ScanDocument;
import gov.iti.jets.NutriScan.repository.ScanRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class AdminService {

    private final ScanRepository scanRepository;
    private final ScanMapper scanMapper;
    private final ElasticsearchClient elasticsearchClient;

    public void reindexAllScans() {

        Pageable pageable = PageRequest.of(0, 100);

        Page<Scan> page;

        do {
            page = scanRepository.findAll(pageable);

            List<ScanDocument> documents = page.stream().map(scanMapper::toDocument).toList();

            BulkRequest request = BulkRequest.of(b -> {

                documents.forEach(
                    doc -> b.operations(
                        op -> op.index(
                            i -> i.index("scans").id(doc.getId().toString()).document(doc))));

                return b;
            });

            try {
                elasticsearchClient.bulk(request);
            } catch (IOException e) {
                log.error("Failed inserting batch in elasticsearch {}", e.getMessage());
            }

            pageable = pageable.next();
        } while (!page.isLast());
    }
}
