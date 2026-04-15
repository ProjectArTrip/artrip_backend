package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.constants.NotificationAction;
import org.atdev.artrip.service.dto.result.UserNoticeResult;

import java.time.LocalDateTime;

public record UserNoticeResponse(
        Long userNoticeId,
        NotificationAction action,
        Long referenceId,
        String title,
        String body,
        boolean isRead,
        LocalDateTime createdAt
) {

    public static UserNoticeResponse from(UserNoticeResult result) {
        return new UserNoticeResponse(
                result.userNoticeId(),
                result.action(),
                result.referenceId(),
                result.title(),
                result.body(),
                result.isRead(),
                result.createdAt()
        );
    }
}
