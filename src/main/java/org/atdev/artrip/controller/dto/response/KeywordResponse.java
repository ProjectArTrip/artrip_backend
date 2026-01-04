package org.atdev.artrip.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.atdev.artrip.constants.KeywordType;

@Getter
@Builder
@AllArgsConstructor
public class KeywordResponse {
    private Long keywordId;
    private String name;
    private KeywordType type;
}
