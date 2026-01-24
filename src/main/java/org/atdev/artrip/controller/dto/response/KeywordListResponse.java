package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.service.dto.result.KeywordResult;

import java.util.List;

public record KeywordListResponse(
        List<KeywordResponse> keywords
) {

    public static KeywordListResponse from(List<KeywordResult> results) {
        return new KeywordListResponse(
                results.stream().map(KeywordResponse::from).toList());
    }
}
