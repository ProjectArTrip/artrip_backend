package org.atdev.artrip.global.apipayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.atdev.artrip.global.apipayload.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FavoriteError implements BaseErrorCode {

    _FAVORITE_NOT_FOUND(HttpStatus.NOT_FOUND, "FAVORITE404-NOT_FOUND", "즐겨찾기를 찾을 수 없습니다."),
    _FAVORITE_ALREADY_EXISTS(HttpStatus.CONFLICT, "FAVORITE409-ALREADY_EXISTS", "이미 즐겨찾기에 추가된 전시입니다."),
    _FAVORITE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "FAVORITE4002-LIMIT_EXCEEDED", "즐겨찾기는 최대 100개까지 추가할 수 있습니다."),
    _EXHIBIT_NOT_FOUND_FOR_FAVORITE(HttpStatus.NOT_FOUND, "FAVORITE404-EXHIBIT_NOT_FOUND", "즐겨찾기하려는 전시를 찾을 수 없습니다."),
    _FAVORITE_UNAUTHORIZED(HttpStatus.FORBIDDEN, "FAVORITE403-UNAUTHORIZED", "다른 사용자의 즐겨찾기에 접근할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
