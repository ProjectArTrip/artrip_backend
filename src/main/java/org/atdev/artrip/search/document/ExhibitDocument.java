package org.atdev.artrip.search.document;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;


@Data
@Document(indexName = "exhibits")
public class ExhibitDocument {

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
