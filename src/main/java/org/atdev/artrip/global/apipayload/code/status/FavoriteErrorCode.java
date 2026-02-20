package org.atdev.artrip.global.apipayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.atdev.artrip.global.apipayload.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FavoriteErrorCode implements BaseErrorCode {

    _UNSUPPORTED_SORT_TYPE(HttpStatus.UNPROCESSABLE_ENTITY, "FAVORITE422-UNPROCESSABLE_ENTITY", "해당 기능에서는 인기순(POPULAR) 정렬을 지원하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
