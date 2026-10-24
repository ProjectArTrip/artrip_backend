package org.atdev.artrip.infra.notification.outbox.handler;

import org.atdev.artrip.domain.review.event.ReviewDeleteByAdminEvent;
import org.atdev.artrip.infra.fcm.FcmNotificationService;
import org.atdev.artrip.infra.notification.NotificationMessage;
import org.atdev.artrip.infra.notification.NotificationReference;
import org.atdev.artrip.infra.notification.NotificationTemplate;
import org.atdev.artrip.infra.notification.outbox.OutboxEventType;
import org.atdev.artrip.service.UserNoticeService;
import org.springframework.stereotype.Component;

@Component
public class ReviewDeletedByAdminNotificationHandler extends SingleUserNotificationHandler<ReviewDeleteByAdminEvent> {

    public ReviewDeletedByAdminNotificationHandler(UserNoticeService userNoticeService, FcmNotificationService fcmNotificationService) {
        super(userNoticeService, fcmNotificationService);
    }

    @Override
    public OutboxEventType type() {
        return OutboxEventType.REVIEW_DELETED_BY_ADMIN;
    }

    @Override
    protected Long userId(ReviewDeleteByAdminEvent event) {
        return event.userId();
    }

    @Override
    protected NotificationMessage message(ReviewDeleteByAdminEvent event) {
        NotificationTemplate tpl = NotificationTemplate.REVIEW_DELETED_BY_ADMIN;
        return NotificationMessage.of(
                NotificationReference.none(),
                tpl.getTitle(),
                tpl.getBody()
        );
    }
}
