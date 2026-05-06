package org.atdev.artrip.infra.fcm.service.listener;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.infra.fcm.service.FcmNotificationService;
import org.atdev.artrip.infra.fcm.service.event.NoticeCreatedEvent;
import org.atdev.artrip.infra.notification.NotificationMessage;
import org.atdev.artrip.infra.notification.NotificationReference;
import org.atdev.artrip.service.UserNoticeService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class FcmNoticeEventListener {

    private final FcmNotificationService fcmNotificationService;
    private final UserNoticeService userNoticeService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(NoticeCreatedEvent event) {
        userNoticeService.createNoticeNotification(event);
        fcmNotificationService.sendNoticeMessage(event.noticeId(), event.title(), event.content());
    }

    public void onCreated(NoticeCreatedEvent event) {
        NotificationMessage message = NotificationMessage.of(
                NotificationReference.notice(event.noticeId()),
                event.title(),
                event.content()
        );
        userNoticeService.broadcast(message);
        fcmNotificationService.sendBroadcast(message);
    }
}
