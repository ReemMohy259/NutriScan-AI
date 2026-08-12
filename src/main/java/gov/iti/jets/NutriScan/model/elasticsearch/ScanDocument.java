package gov.iti.jets.NutriScan.model.elasticsearch;

import gov.iti.jets.NutriScan.dto.ai.ScanStatus;
import gov.iti.jets.NutriScan.dto.ai.Verdict;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "scans")
public class ScanDocument {

    @Id
    @Field(type = FieldType.Keyword)
    private UUID id;

    @Field(type = FieldType.Keyword)
    private UUID userId;

    @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "standard"), otherFields = {
            @InnerField(suffix = "suggest", type = FieldType.Search_As_You_Type),
            @InnerField(suffix = "keyword", type = FieldType.Keyword)})
    private String productName;

    @Field(type = FieldType.Keyword)
    private Verdict verdict;

    @Field(type = FieldType.Keyword)
    private ScanStatus scanStatus;

    @Field(type = FieldType.Date)
    private Instant scannedAt;
}