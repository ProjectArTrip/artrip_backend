package org.atdev.artrip.global.apipayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.atdev.artrip.global.apipayload.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserError implements BaseErrorCode {

    _USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404-NOT_FOUND", "존재하지 않는 회원입니다."),
    _INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "JWT401-INVALID_REFRESH", "리프레시 토큰이 유효하지 않습니다."),
    _SOCIAL_VERIFICATION_FAILED(HttpStatus.UNAUTHORIZED, "SOCIAL401-VERIFICATION_FAILED", "소셜 토큰 검증 중 오류가 발생했습니다."),
    _SOCIAL_ID_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "IDTOKEN401-INVALID", "소셜 ID 토큰이 유효하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
