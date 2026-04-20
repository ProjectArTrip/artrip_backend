package org.atdev.artrip.global.apipayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NoticeStatusCode {

    NOTICE_CREATE_NO_PUSH_TARGET("공지사항이 생성되었으나 알림 대상 유저가 없습니다 (FCM 토큰 미등록 또는 알림 비허용)"),
    NOTICE_CREATE_WITH_PUSH("공지사항이 생성되었고 %d명에게 알림이 발송됩니다");

    private final String message;

    public String format(Object... args) {
        return String.format(this.message, args);
    }
}
