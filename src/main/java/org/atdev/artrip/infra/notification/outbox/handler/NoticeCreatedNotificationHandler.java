package org.atdev.artrip.infra.notification.outbox.handler;

import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.notice.event.NoticeCreatedEvent;
import org.atdev.artrip.infra.fcm.FcmNotificationService;
import org.atdev.artrip.infra.notification.NotificationMessage;
import org.atdev.artrip.infra.notification.NotificationReference;
import org.atdev.artrip.infra.notification.PushProgress;
import org.atdev.artrip.infra.notification.outbox.OutboxEventType;
import org.atdev.artrip.service.UserNoticeService;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class NoticeCreatedNotificationHandler implements NotificationHandler<NoticeCreatedEvent> {

    private final UserNoticeService userNoticeService;
    private final FcmNotificationService fcmNotificationService;

    @Override
    public Set<OutboxEventType> types() {
        return Set.of(OutboxEventType.NOTICE_CREATED);
    }

    @Override
    public void saveInbox(NoticeCreatedEvent event) {
        userNoticeService.broadcast(message(event));
    }

    @Override
    public void sendPush(NoticeCreatedEvent event, PushProgress progress) throws FirebaseMessagingException {
        fcmNotificationService.sendBroadcast(message(event), progress);
    }

    private NotificationMessage message(NoticeCreatedEvent event) {
        return NotificationMessage.of(
                NotificationReference.notice(event.noticeId()),
                event.title(),
                "[공지] " + event.title() + "\n" + event.content()
        );
    }
}
