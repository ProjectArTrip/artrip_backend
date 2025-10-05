package org.atdev.artrip.domain.keyword.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.atdev.artrip.domain.Enum.KeywordType;

@Getter
@Builder
@AllArgsConstructor
public class KeywordResponse {
    private Long keywordId;
    private String name;
    private KeywordType type;
}
