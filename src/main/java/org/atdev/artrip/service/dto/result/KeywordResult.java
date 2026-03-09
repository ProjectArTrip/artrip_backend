package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.constants.KeywordType;
import org.atdev.artrip.domain.keyword.Keyword;

public record KeywordResult(
        Long keywordId,
        String name,
        KeywordType type
) {

    public static KeywordResult from(Keyword keyword) {
        return new KeywordResult(keyword.getKeywordId(), keyword.getName(), keyword.getType());
    }
}
