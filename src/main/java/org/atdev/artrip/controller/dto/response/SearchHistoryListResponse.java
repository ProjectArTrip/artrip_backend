package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.service.dto.result.SearchHistoryResult;

import java.util.List;

public record SearchHistoryListResponse(
        List<SearchHistoryResponse> items
) {

    public static SearchHistoryListResponse from(List<SearchHistoryResult> results) {
        return new SearchHistoryListResponse(
                results.stream().map(SearchHistoryResponse::from).toList());
    }
}
