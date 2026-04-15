package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.domain.search.SearchHistory;

import java.time.LocalDateTime;

public record SearchHistoryResult(
        String content,
        LocalDateTime createdAt
) {
    public static SearchHistoryResult from(SearchHistory entity) {
        return new SearchHistoryResult(
                entity.getContent(),
                entity.getCreatedAt()
        );
    }
}
