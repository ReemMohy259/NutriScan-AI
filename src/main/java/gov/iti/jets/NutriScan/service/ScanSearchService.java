package gov.iti.jets.NutriScan.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;
import gov.iti.jets.NutriScan.dto.ScanSearchResult;
import gov.iti.jets.NutriScan.dto.ai.ScanStatus;
import gov.iti.jets.NutriScan.dto.ai.Verdict;
import gov.iti.jets.NutriScan.model.elasticsearch.ScanDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import co.elastic.clients.json.JsonData;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScanSearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticsearchClient elasticsearchClient;

    public ScanSearchResult search(
        UUID userId,
        String query,
        Verdict verdict,
        ScanStatus scanStatus,
        LocalDate date,
        Pageable pageable) {

        BoolQuery.Builder boolQuery = new BoolQuery.Builder();

        // Always filter by owner
        boolQuery.filter(f -> f.term(t -> t.field("userId").value(userId.toString())));

        // Product name search: no fuzzy search when 1 or 2 characters
        if (StringUtils.hasText(query)) {

            String normalized = query.trim();

            boolQuery.must(m -> m.match(match -> {

                match.field("productName");
                match.query(normalized);

                if (normalized.length() >= 3) {
                    match.fuzziness("AUTO");
                }

                return match;
            }));
        }

        // Verdict filter
        if (verdict != null) {
            boolQuery.filter(f -> f.term(t -> t.field("verdict").value(verdict.name())));
        }

        // Scan status filter
        if (scanStatus != null) {
            boolQuery.filter(f -> f.term(t -> t.field("scanStatus").value(scanStatus.name())));
        }

        // Date filter
        if (date != null) {
            ZoneId zone = ZoneId.of("Africa/Cairo");

            Instant from = date.atStartOfDay(zone).toInstant();

            Instant to = date.plusDays(1).atStartOfDay(zone).toInstant();

            boolQuery.filter(
                f -> f.range(
                    r -> r.date(d -> d.field("scannedAt").gte(from.toString()).lt(to.toString()))));
        }

        NativeQuery nativeQuery = NativeQuery.builder()
            .withQuery(q -> q.bool(boolQuery.build()))
            .withPageable(pageable)
            .withSort(sort -> sort.field(f -> f.field("scannedAt").order(SortOrder.Desc)))
            .withSourceFilter(FetchSourceFilter.of(f -> f.withIncludes("id")))
            .build();

        SearchHits<ScanDocument> hits = elasticsearchOperations
            .search(nativeQuery, ScanDocument.class);

        List<UUID> ids = hits.getSearchHits()
            .stream()
            .map(hit -> hit.getContent().getId())
            .toList();

        return new ScanSearchResult(ids, hits.getTotalHits());
    }

    public void deleteAllByUserId(UUID userId) {

        try {
            DeleteByQueryResponse response = elasticsearchClient.deleteByQuery(
                d -> d.index("scans")
                    .query(q -> q.term(t -> t.field("userId").value(userId.toString()))));

            log.info("Deleted {} scan documents for user {}", response.deleted(), userId);
        } catch (IOException e) {
            throw new IllegalStateException(
                "Failed to delete scan documents from Elasticsearch for user " + userId,
                e);
        }
    }

    public List<String> getSuggestions(UUID userId, String query) {

        if (!StringUtils.hasText(query) || query.trim().length() < 2) {
            return List.of();
        }

        String normalized = query.trim();

        BoolQuery.Builder boolQuery = new BoolQuery.Builder();

        // Only suggestions belonging to this user
        boolQuery.filter(f -> f.term(t -> t.field("userId").value(userId.toString())));

        // Autocomplete
        boolQuery.must(
            m -> m.multiMatch(
                mm -> mm.query(normalized)
                    .type(TextQueryType.BoolPrefix)
                    .fields(
                        "productName.suggest",
                        "productName.suggest._2gram",
                        "productName.suggest._3gram")));

        NativeQuery nativeQuery = NativeQuery.builder()
            .withQuery(q -> q.bool(boolQuery.build()))
            .withPageable(PageRequest.of(0, 10))
            .withSourceFilter(FetchSourceFilter.of(f -> f.withIncludes("productName")))
            .withSort(sort -> sort.score(s -> s.order(SortOrder.Desc)))
            .build();

        SearchHits<ScanDocument> hits = elasticsearchOperations
            .search(nativeQuery, ScanDocument.class);

        return hits.getSearchHits()
            .stream()
            .map(hit -> hit.getContent().getProductName())
            .filter(StringUtils::hasText)
            .distinct()
            .limit(10)
            .toList();
    }
}