package org.atdev.artrip.global.apipayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.atdev.artrip.global.apipayload.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserError implements BaseErrorCode {

    // User Errors
    _USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404-NOT_FOUND", "존재하지 않는 회원입니다."),
    _USER_FORBIDDEN(HttpStatus.FORBIDDEN, "USER403-FORBIDDEN", "접근 권한이 없습니다."),
    _DUPLICATE_NICKNAME(HttpStatus.CONFLICT,"USER409-CONFLICT","닉네임이 중복되었습니다."),
    _PROFILE_IMAGE_NOT_EXIST(HttpStatus.NOT_FOUND,"USER404-NOT_FOUND","프로필 이미지가 존재하지 않습니다."),
    _NICKNAME_BAD_REQUEST(HttpStatus.BAD_REQUEST,"USER400-BAD_REQUEST","닉네임 형식이 올바르지 않습니다."),

    // JWT Errors
    _JWT_EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "JWT401-EXPIRED_ACCESS", "만료된 엑세스 토큰입니다."),
    _JWT_INVALID_SIGNATURE(HttpStatus.UNAUTHORIZED, "JWT401-INVALID_SIGNATURE", "잘못된 JWT 서명입니다."),
    _JWT_UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "JWT401-UNSUPPORTED", "지원하지 않는 JWT 토큰입니다."),
    _JWT_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "JWT401-INVALID", "JWT 토큰이 잘못되었습니다."),

    // JWT Refresh Token Errors
    _JWT_EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "JWT401-EXPIRED_REFRESH", "만료된 리프레시 토큰입니다."),
    _INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "JWT401-INVALID_REFRESH", "리프레시 토큰이 유효하지 않습니다."),
    _INVALID_USER_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "JWT401-INVALID_USER_REFRESH", "리프레시 토큰에 유저ID가 유효하지 않습니다."),

    // Social Login Errors
    _SOCIAL_VERIFICATION_FAILED(HttpStatus.UNAUTHORIZED, "SOCIAL401-VERIFICATION_FAILED", "소셜 토큰 검증 중 오류가 발생했습니다."),
    _SOCIAL_ID_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "IDTOKEN401-INVALID", "소셜 ID 토큰이 유효하지 않습니다."),
    _SOCIAL_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "SOCIAL401-TOKEN_EXPIRED", "소셜 ID 토큰이 만료되었습니다."),
    _SOCIAL_TOKEN_INVALID_SIGNATURE(HttpStatus.UNAUTHORIZED, "SOCIAL401-INVALID_SIGNATURE", "소셜 토큰 서명이 유효하지 않습니다."),
    _SOCIAL_TOKEN_INVALID_AUDIENCE(HttpStatus.UNAUTHORIZED, "SOCIAL401-INVALID_AUDIENCE", "소셜 토큰의 aud 값이 일치하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
