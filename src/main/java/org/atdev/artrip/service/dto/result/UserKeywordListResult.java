package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.domain.keyword.UserKeyword;

import java.util.List;

public record UserKeywordListResult(
        List<UserKeywordResult> keywords
) {

    public static KeywordListResult fromUserKeywords(List<UserKeyword> userKeywords) {
        return new KeywordListResult(
                userKeywords.stream()
                        .map(UserKeywordResult::from)
                        .toList()
        );
    }
}
