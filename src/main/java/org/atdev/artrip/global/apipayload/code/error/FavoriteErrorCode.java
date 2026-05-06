package org.atdev.artrip.global.apipayload.code.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.atdev.artrip.global.apipayload.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FavoriteErrorCode implements BaseErrorCode {

    _UNSUPPORTED_SORT_TYPE(HttpStatus.UNPROCESSABLE_ENTITY, "FAVORITE422-UNPROCESSABLE_ENTITY", "해당 기능에서는 인기순(POPULAR) 정렬을 지원하지 않습니다."),
    _FAVORITE_ALREADY_EXISTS(HttpStatus.CONFLICT, "FAVORITE409-ALREADY_EXISTS", "이미 즐겨찾기에 추가된 전시입니다."),
    _FAVORITE_NOT_FOUND(HttpStatus.NOT_FOUND, "FAVORITE404-NOT_FOUND", "즐겨찾기 항목을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
