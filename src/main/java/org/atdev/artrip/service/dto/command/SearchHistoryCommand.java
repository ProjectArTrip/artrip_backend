package org.atdev.artrip.service.dto.command;

public record SearchHistoryCommand(
        Long userId,
        Long searchHistoryId
) {
}
