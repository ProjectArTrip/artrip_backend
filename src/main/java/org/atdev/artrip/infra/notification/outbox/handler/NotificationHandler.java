package org.atdev.artrip.infra.notification.outbox.handler;

import com.google.firebase.messaging.FirebaseMessagingException;
import org.atdev.artrip.infra.notification.PushProgress;
import org.atdev.artrip.infra.notification.outbox.OutboxEventType;

import java.util.Set;

/**
 * outbox 이벤트에 대한 알림 처리. 알림함 저장과 푸시 발송을 단계별로 나눠, 재시도 시 끝난 단계는 건너뛴다.
 */
public interface NotificationHandler<E> {

    /** 이 handler가 처리하는 이벤트 타입 */
    Set<OutboxEventType> types();

    /** 알림함(UserNotice) 저장. outbox의 inbox_saved 갱신과 같은 트랜잭션에서 호출된다. */
    void saveInbox(E event);

    /** 푸시 발송. 청크 단위로 progress를 전진시켜, 중간에 실패하면 다음 시도가 이어서 보낸다. */
    void sendPush(E event, PushProgress progress) throws FirebaseMessagingException;
}
