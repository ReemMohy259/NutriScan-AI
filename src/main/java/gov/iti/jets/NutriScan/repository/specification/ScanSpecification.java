package gov.iti.jets.NutriScan.repository.specification;

import gov.iti.jets.NutriScan.dto.ai.ScanStatus;
import gov.iti.jets.NutriScan.dto.ai.Verdict;
import gov.iti.jets.NutriScan.model.Scan;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

public final class ScanSpecification {

    private ScanSpecification() {
    }

    public static Specification<Scan> search(
        UUID userId,
        String query,
        Verdict verdict,
        ScanStatus scanStatus,
        LocalDate date) {

        return (root, cq, cb) -> {

            Predicate predicate = cb.equal(root.get("user").get("id"), userId);

            if (StringUtils.hasText(query)) {

                predicate = cb.and(
                    predicate,
                    cb.like(cb.lower(root.get("productName")), "%" + query.toLowerCase() + "%"));
            }

            if (verdict != null) {

                predicate = cb.and(predicate, cb.equal(root.get("verdict"), verdict));
            }

            if (scanStatus != null) {

                predicate = cb.and(predicate, cb.equal(root.get("status"), scanStatus));
            }

            if (date != null) {
                ZoneId zone = ZoneId.of("Africa/Cairo");

                Instant start = date.atStartOfDay(zone).toInstant();

                Instant end = date.plusDays(1).atStartOfDay(zone).toInstant();

                predicate = cb.and(
                    predicate,
                    cb.greaterThanOrEqualTo(root.get("scannedAt"), start),
                    cb.lessThan(root.get("scannedAt"), end));
            }

            return predicate;
        };
    }
}