package org.atdev.artrip.elastic.document;

import jakarta.persistence.Id;
import lombok.Data;
<<<<<<< HEAD:src/main/java/org/atdev/artrip/search/document/ExhibitDocument.java
import org.atdev.artrip.domain.Enum.Genre;
import org.atdev.artrip.domain.Enum.Status;
import org.springframework.data.elasticsearch.annotations.DateFormat;
=======
>>>>>>> developer:src/main/java/org/atdev/artrip/elastic/document/ElasticDocument.java
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

    private Status status;
    private String posterUrl;
    private String ticketUrl;
    private Genre genre;
    private BigDecimal latitude;
    private BigDecimal longitude;

}
