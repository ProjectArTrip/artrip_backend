package org.atdev.artrip.global.apipayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.atdev.artrip.global.apipayload.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {

    // Auth Errors
    _UNSUPPORTED_SOCIAL_PROVIDER(HttpStatus.BAD_REQUEST, "AUTH400-UNSUPPORTED_PROVIDER", "지원하지 않는 소셜 로그인입니다."),
    _SOCIAL_EMAIL_NOT_PROVIDED(HttpStatus.BAD_REQUEST, "AUTH400-EMAIL_NOT_PROVIDED", "소셜 로그인에서 이메일 정보를 제공받지 못했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
