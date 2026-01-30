package org.atdev.artrip.service.dto.command;

public record SearchHistoryCommand(
        Long userId,
        Long searchHistoryId,
        String content
) {

    public static SearchHistoryCommand from (Long userId) {
        return new SearchHistoryCommand(userId, null, null);
    }

    public static SearchHistoryCommand create(Long userId, String content){
        return new SearchHistoryCommand(userId, null, content);
    }
    public static SearchHistoryCommand of(Long userId, Long searchHistoryId) {
        return new SearchHistoryCommand(userId, searchHistoryId, null);
    }
}
