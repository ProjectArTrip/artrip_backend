package org.atdev.artrip.service.dto.command;

public record AdminNoticeUpdateCommand(
        Long userId,
        Long noticeId,
        String title,
        String content
) {
    public static AdminNoticeUpdateCommand toCommand(Long userId, Long noticeId, String title, String content) {
        return new AdminNoticeUpdateCommand(userId, noticeId, title, content);
    }
}
