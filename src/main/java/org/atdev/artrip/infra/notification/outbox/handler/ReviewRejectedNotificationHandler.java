package org.atdev.artrip.infra.notification.outbox.handler;

import org.atdev.artrip.domain.review.event.ReviewRejectedEvent;
import org.atdev.artrip.infra.fcm.FcmNotificationService;
import org.atdev.artrip.infra.notification.NotificationMessage;
import org.atdev.artrip.infra.notification.NotificationReference;
import org.atdev.artrip.infra.notification.NotificationTemplate;
import org.atdev.artrip.infra.notification.outbox.OutboxEventType;
import org.atdev.artrip.service.UserNoticeService;
import org.springframework.stereotype.Component;

@Component
public class ReviewRejectedNotificationHandler extends SingleUserNotificationHandler<ReviewRejectedEvent> {

    public ReviewRejectedNotificationHandler(UserNoticeService userNoticeService, FcmNotificationService fcmNotificationService) {
        super(userNoticeService, fcmNotificationService);
    }

    @Override
    public OutboxEventType type() {
        return OutboxEventType.REVIEW_REJECTED;
    }

    @Override
    protected Long userId(ReviewRejectedEvent event) {
        return event.userId();
    }

    @Override
    protected NotificationMessage message(ReviewRejectedEvent event) {
        NotificationTemplate tpl = NotificationTemplate.REVIEW_REJECTED;
        return NotificationMessage.of(
                NotificationReference.reviewEdit(event.reviewId()),
                tpl.getTitle(),
                tpl.formatBody(event.exhibitTitle(), event.reviewContent(), event.reason())
        );
    }
}
