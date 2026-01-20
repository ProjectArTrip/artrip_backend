package org.atdev.artrip.service.dto.command;

import lombok.Builder;

import java.util.List;

@Builder
public record KeywordCommand(
        List<String> keywords,
        Long userId
) {
}
