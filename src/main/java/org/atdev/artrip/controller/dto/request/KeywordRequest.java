package org.atdev.artrip.controller.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class KeywordRequest {
    private List<Long> keywordIds;
}