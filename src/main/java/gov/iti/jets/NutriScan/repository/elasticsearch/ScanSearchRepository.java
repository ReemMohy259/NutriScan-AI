package gov.iti.jets.NutriScan.repository.elasticsearch;

import gov.iti.jets.NutriScan.model.elasticsearch.ScanDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.UUID;

public interface ScanSearchRepository extends ElasticsearchRepository<ScanDocument, UUID> {

}