package org.atdev.artrip.elastic.document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "search_history")
public class SearchHistoryDocument {

    @Id
    private String id;

    private Long userId;
    private String content;

    @Field(type = FieldType.Date, format = DateFormat.epoch_millis)
    private LocalDateTime createdAt;
}

