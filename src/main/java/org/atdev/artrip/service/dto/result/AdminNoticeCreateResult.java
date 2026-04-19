package org.atdev.artrip.service.dto.result;

public record AdminNoticeCreateResult(
        Long noticeId,
        long pushUserCount,
        String message
) {
    public static AdminNoticeCreateResult of(Long noticeId, long pushUserCount, String message) {
        return new AdminNoticeCreateResult(noticeId, pushUserCount, message);
    }
}
