package org.atdev.artrip.global.apipayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.atdev.artrip.global.apipayload.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FcmErrorCode implements BaseErrorCode {

    _INVALID_REQUEST_MESSAGE(HttpStatus.INTERNAL_SERVER_ERROR, "FCM_INVALID_ERROR-MESSAGE", "올바르지 않은 FCM 메시지 형식입니다."),
    _INVALID_REQUEST_URI(HttpStatus.INTERNAL_SERVER_ERROR, "FCM_INVALID_ERROR-URI", "유효하지 않은 알림 대상 주소(URI)입니다."),
    _INVALID_REQUEST_PATTERN(HttpStatus.INTERNAL_SERVER_ERROR, "FCM_PATTERN_ERROR", "FCM 토큰 또는 데이터 패턴이 일치하지 않습니다." ),
    _FCM_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "FCM_SERVICE_UNAVAILABLE", "FCM 서버와의 통신이 원활하지 않습니다." );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
