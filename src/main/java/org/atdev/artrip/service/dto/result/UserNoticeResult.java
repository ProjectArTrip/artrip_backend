package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.constants.NotificationAction;
import org.atdev.artrip.domain.notice.UserNotice;

import java.time.LocalDateTime;

public record UserNoticeResult(
        Long userNoticeId,
        NotificationAction action,
        Long referenceId,
        String title,
        String body,
        boolean isRead,
        LocalDateTime createdAt
) {
    public static UserNoticeResult from(UserNotice userNotice) {
        return new UserNoticeResult(
                userNotice.getUserNoticeId(),
                userNotice.getAction(),
                userNotice.getReferenceId(),
                userNotice.getTitle(),
                userNotice.getBody(),
                userNotice.isRead(),
                userNotice.getCreatedAt()
        );
    }
}
