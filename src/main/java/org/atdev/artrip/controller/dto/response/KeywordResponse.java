package org.atdev.artrip.controller.dto.response;

import lombok.Builder;
import org.atdev.artrip.constants.KeywordType;
import org.atdev.artrip.service.dto.result.KeywordResult;

@Builder
public record KeywordResponse(
        Long keywordId,
        String name,
        KeywordType type) {

    public static KeywordResponse from(KeywordResult result) {

        return new KeywordResponse(result.keywordId(), result.name(), result.type());
    }

}
