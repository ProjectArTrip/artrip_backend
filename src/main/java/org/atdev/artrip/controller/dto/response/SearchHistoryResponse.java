package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.service.dto.result.SearchHistoryResult;

public record SearchHistoryResponse(
        String content
) {

    public static SearchHistoryResponse from(SearchHistoryResult result) {
        return new SearchHistoryResponse(
                result.content()
        );
    }
}
