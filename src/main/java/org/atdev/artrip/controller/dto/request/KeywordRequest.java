package org.atdev.artrip.controller.dto.request;

import org.atdev.artrip.service.dto.command.KeywordCommand;

import java.util.List;

public record KeywordRequest(
        List<String> keywords) {
    public KeywordCommand toCommand(Long userId) {
        return KeywordCommand.builder()
                .userId(userId)
                .keywords(this.keywords)
                .build();
    }
}