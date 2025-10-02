package org.atdev.artrip.elastic.document;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

import java.math.BigDecimal;


@Data
@Document(indexName = "exhibits")
public class ElasticDocument {

    @Id
    private Long id;

    private String title;
    private String description;

//    @Field(type = FieldType.Date, format = DateFormat.epoch_millis)
    private Long startDate;

//    @Field(type = FieldType.Date, format = DateFormat.epoch_millis)
    private Long endDate;

    private String status;
    private String posterUrl;
    private String ticketUrl;
    private String genre;
    private BigDecimal latitude;
    private BigDecimal longitude;

}
