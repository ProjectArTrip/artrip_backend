package org.atdev.artrip.global.apipayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.atdev.artrip.global.apipayload.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum NoticeErrorCode implements BaseErrorCode {
    _NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTICE404-NOT_FOUND","공지사항을 찾을 수 없습니다."),
    _USER_NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOTICE404-NOT_FOUND", "알림을 찾을 수 없습니다."),
    _USER_NOTICE_FORBIDDEN(HttpStatus.FORBIDDEN, "USER_NOTICE403-FORBIDDEN", "해당 알림에 대한 접근 권한이 없습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
