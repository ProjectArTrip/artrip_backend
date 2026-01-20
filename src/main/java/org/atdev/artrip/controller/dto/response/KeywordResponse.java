package org.atdev.artrip.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.atdev.artrip.constants.KeywordType;
import org.atdev.artrip.service.dto.result.KeywordResult;

import java.util.List;

@Builder
public record KeywordResponse(
        Long keywordId,
        String name,
        KeywordType type) {

    public static KeywordResponse from(KeywordResult result) {

        return KeywordResponse.builder()
                .keywordId(result.keywordId())
                .name(result.name())
                .type(result.type())
                .build();
    }

    public static List<KeywordResponse> from(List<KeywordResult> results){

        return results.stream()
                .map(KeywordResponse::from)
                .toList();
    }

}
