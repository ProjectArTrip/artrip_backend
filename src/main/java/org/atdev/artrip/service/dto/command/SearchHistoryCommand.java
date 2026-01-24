package org.atdev.artrip.service.dto.command;

import java.time.LocalDate;

public record SearchHistoryCommand(
        Long userId,
        Long searchHistoryId
) {
    public static SearchHistoryCommand of(Long userId) {
        return new SearchHistoryCommand(userId, null);
    }

    public static SearchHistoryCommand forDelete(Long userId, Long searchHistoryId){
        return new SearchHistoryCommand(userId, searchHistoryId);
    }
}
