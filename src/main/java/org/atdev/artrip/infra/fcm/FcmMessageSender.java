package org.atdev.artrip.infra.fcm;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * FCM 동기 발송. 호출 스레드(outbox 워커)에서 결과를 받아 성공/실패를 바로 판단한다.
 */
@Component
@RequiredArgsConstructor
public class FcmMessageSender {

    private final FirebaseMessaging firebaseMessaging;

    public String sendSingle(NotificationSingleDispatch dispatch) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setToken(dispatch.targetToken())
                .setNotification(dispatch.toNotification())
                .putAllData(dispatch.data())
                .setApnsConfig(dispatch.toApnsConfig())
                .build();

        return firebaseMessaging.send(message);
    }

    /** 토큰은 최대 500개까지. 호출하는 쪽에서 나눠서 넘긴다. */
    public BatchResponse sendMulticast(NotificationMulticastDispatch dispatch) throws FirebaseMessagingException {
        MulticastMessage message = MulticastMessage.builder()
                .setNotification(dispatch.toNotification())
                .addAllTokens(dispatch.targetTokens())
                .putAllData(dispatch.data())
                .setApnsConfig(dispatch.toApnsConfig())
                .build();

        return firebaseMessaging.sendEachForMulticast(message);
    }
}
