package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.service.dto.result.SearchHistoryResult;

import java.time.LocalDate;

public record SearchHistoryResponse(
        Long searchHistoryId,
        String content,
        LocalDate createdAt
) {

    public static SearchHistoryResponse from(SearchHistoryResult result) {
        return new SearchHistoryResponse(
                result.searchHistoryId(),
                result.content(),
                result.createdAt()
        );
    }
}
