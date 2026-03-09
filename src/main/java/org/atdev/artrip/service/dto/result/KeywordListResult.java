package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.domain.keyword.Keyword;

import java.util.List;

public record KeywordListResult(
        List<KeywordResult> keywords
) {
    public static KeywordListResult fromKeywords(List<Keyword> keywords) {
        return new KeywordListResult(
                keywords.stream()
                        .map(KeywordResult::from)
                        .toList()
        );
    }
}