package org.atdev.artrip.infra.notification;

/**
 * 푸시 발송 진행 위치. 재시도 시 이미 보낸 사용자 다음부터 이어서 보내기 위해 사용한다.
 */
public interface PushProgress {

    /** 마지막으로 발송을 마친 user_id. 아직 보낸 적이 없으면 null */
    Long cursor();

    void advance(Long lastUserId);
}
