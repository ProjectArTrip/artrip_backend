package org.atdev.artrip.infra.fcm;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.notice.event.NoticeCreatedEvent;
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
    public void onCreated(NoticeCreatedEvent event) {
        NotificationMessage message = NotificationMessage.of(
                NotificationReference.notice(event.noticeId()),
                event.title(),
                "[공지]" + event.title() + "\n" + event.content()
        );
        userNoticeService.broadcast(message);
        fcmNotificationService.sendBroadcast(message);
    }
}
