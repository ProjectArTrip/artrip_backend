package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.service.dto.result.SearchHistoryResult;

import java.time.LocalDateTime;

public record SearchHistoryResponse(
        String content,
        LocalDateTime createdAt
) {

    public static SearchHistoryResponse from(SearchHistoryResult result) {
        return new SearchHistoryResponse(
                result.content(),
                result.createdAt()
        );
    }
}
