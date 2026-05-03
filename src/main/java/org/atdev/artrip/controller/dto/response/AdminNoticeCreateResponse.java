package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.service.dto.result.AdminNoticeCreateResult;

public record AdminNoticeCreateResponse(
        Long noticeId,
        long pushUserCount,
        String message
) {
    public static AdminNoticeCreateResponse from(AdminNoticeCreateResult result) {
        return new AdminNoticeCreateResponse(
                result.noticeId(),
                result.pushUserCount(),
                result.message()
        );
    }
}
