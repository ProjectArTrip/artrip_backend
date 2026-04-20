package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.domain.search.SearchHistory;

import java.time.LocalDateTime;

public record SearchHistoryResult(
        Long SearchHistoryId,
        String content,
        LocalDateTime createdAt
) {
    public static SearchHistoryResult from(SearchHistory entity) {
        return new SearchHistoryResult(
                entity.getSearchHistoryId(),
                entity.getContent(),
                entity.getCreatedAt()
        );
    }
}
