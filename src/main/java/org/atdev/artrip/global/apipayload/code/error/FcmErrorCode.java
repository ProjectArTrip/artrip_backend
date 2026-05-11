package org.atdev.artrip.global.apipayload.code.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.atdev.artrip.global.apipayload.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FcmErrorCode implements BaseErrorCode {

    _INVALID_REQUEST_PATTERN(HttpStatus.BAD_REQUEST, "FCM400-PATTERN_ERROR", "FCM 토큰 또는 데이터 패턴이 일치하지 않습니다." ),
    _INVALID_REQUEST_MESSAGE(HttpStatus.INTERNAL_SERVER_ERROR, "FCM500-INVALID_ERROR-MESSAGE", "올바르지 않은 FCM 메시지 형식입니다."),
    _FCM_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"FCM500-SERVER_ERROR","FCM 서버 에러");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
