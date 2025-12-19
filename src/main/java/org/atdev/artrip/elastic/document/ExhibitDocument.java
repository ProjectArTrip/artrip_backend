package org.atdev.artrip.elastic.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.atdev.artrip.domain.Enum.Status;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "exhibits")
public class ExhibitDocument {

    @Id
    private Long id;

    private String title;
    private String description;

    @Field(type = FieldType.Date, format = DateFormat.date)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    private LocalDate startDate;

    @Field(type = FieldType.Date, format = DateFormat.date)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    private LocalDate endDate;

    private Status status;
    private String posterUrl;
    private String ticketUrl;

    private BigDecimal latitude;
    private BigDecimal longitude;

    private List<KeywordInfo> keywords;

}
