package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.domain.keyword.Keyword;
import org.atdev.artrip.domain.keyword.UserKeyword;

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

    public static KeywordListResult fromUserKeywords(List<UserKeyword> userKeywords) {
        return new KeywordListResult(
                userKeywords.stream()
                        .map(KeywordResult::from)
                        .toList()
        );
    }
}