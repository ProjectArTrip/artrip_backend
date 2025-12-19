package org.atdev.artrip.domain.search.web.dto.response;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.atdev.artrip.domain.auth.data.User;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class SearchHistoryResponse {

    @Id
    private Long searchHistoryId;
    private User user;
    private LocalDateTime createdAt;
    private String content;
}
