package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.service.dto.result.KeywordListResult;
import org.atdev.artrip.service.dto.result.KeywordResult;

import java.util.List;

public record KeywordListResponse(
        List<KeywordResponse> keywords
) {

    public static KeywordListResponse from(KeywordListResult keywords) {

        return new KeywordListResponse(
                keywords.keywords().stream()
                .map(KeywordResponse::from)
                .toList());
    }
}
