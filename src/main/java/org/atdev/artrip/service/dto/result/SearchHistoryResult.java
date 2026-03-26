package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.domain.search.SearchHistory;

public record SearchHistoryResult(
        String content
) {
    public static SearchHistoryResult from(SearchHistory entity) {
        return new SearchHistoryResult(
                entity.getContent()
        );
    }
}
