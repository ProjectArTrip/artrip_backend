package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.domain.search.SearchHistory;

import java.time.LocalDate;

public record SearchHistoryResult(
        Long searchHistoryId,
        String content,
        LocalDate createdAt
) {
    public static SearchHistoryResult from(SearchHistory entity) {
        return new SearchHistoryResult(
                entity.getSearchHistoryId(),
                entity.getContent(),
                entity.getCreatedAt()
        );
    }
}
