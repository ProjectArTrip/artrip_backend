package org.atdev.artrip.infra.notification.outbox.handler;

import org.atdev.artrip.domain.review.event.ReviewApprovedEvent;
import org.atdev.artrip.infra.fcm.FcmNotificationService;
import org.atdev.artrip.infra.notification.NotificationMessage;
import org.atdev.artrip.infra.notification.NotificationReference;
import org.atdev.artrip.infra.notification.NotificationTemplate;
import org.atdev.artrip.infra.notification.outbox.OutboxEventType;
import org.atdev.artrip.service.UserNoticeService;
import org.springframework.stereotype.Component;

@Component
public class ReviewApprovedNotificationHandler extends SingleUserNotificationHandler<ReviewApprovedEvent> {

    public ReviewApprovedNotificationHandler(UserNoticeService userNoticeService, FcmNotificationService fcmNotificationService) {
        super(userNoticeService, fcmNotificationService);
    }

    @Override
    public OutboxEventType type() {
        return OutboxEventType.REVIEW_APPROVED;
    }

    @Override
    protected Long userId(ReviewApprovedEvent event) {
        return event.userId();
    }

    @Override
    protected NotificationMessage message(ReviewApprovedEvent event) {
        NotificationTemplate tpl = NotificationTemplate.REVIEW_APPROVED;
        return NotificationMessage.of(
                NotificationReference.exhibit(event.exhibitId()),
                tpl.getTitle(),
                tpl.formatBody(event.exhibitTitle())
        );
    }
}
