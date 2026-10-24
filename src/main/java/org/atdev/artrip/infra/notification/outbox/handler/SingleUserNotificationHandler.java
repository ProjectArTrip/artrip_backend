package org.atdev.artrip.infra.notification.outbox.handler;

import com.google.firebase.messaging.FirebaseMessagingException;
import org.atdev.artrip.infra.fcm.FcmNotificationService;
import org.atdev.artrip.infra.notification.NotificationMessage;
import org.atdev.artrip.infra.notification.PushProgress;
import org.atdev.artrip.service.UserNoticeService;

/**
 * 사용자 한 명에게 가는 알림(리뷰, 제보)의 공통 처리.
 */
public abstract class SingleUserNotificationHandler<E> implements NotificationHandler<E> {

    private final UserNoticeService userNoticeService;
    private final FcmNotificationService fcmNotificationService;

    protected SingleUserNotificationHandler(UserNoticeService userNoticeService,
                                            FcmNotificationService fcmNotificationService) {
        this.userNoticeService = userNoticeService;
        this.fcmNotificationService = fcmNotificationService;
    }

    protected abstract Long userId(E event);

    protected abstract NotificationMessage message(E event);

    @Override
    public void saveInbox(E event) {
        userNoticeService.saveSingle(userId(event), message(event));
    }

    @Override
    public void sendPush(E event, PushProgress progress) throws FirebaseMessagingException {
        if (progress.cursor() != null) return;

        Long userId = userId(event);
        fcmNotificationService.sendToUser(userId, message(event));
        progress.advance(userId);
    }
}
