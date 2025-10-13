package org.atdev.artrip.domain.keyword.web.dto;

import lombok.Getter;
import org.atdev.artrip.domain.keyword.data.Keyword;

import java.util.List;

@Getter
public class KeywordRequest {
    private List<Long> keywordIds;
}