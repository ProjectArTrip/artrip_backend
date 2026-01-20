package org.atdev.artrip.global.apipayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.atdev.artrip.global.apipayload.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum KeywordErrorCode implements BaseErrorCode {

    _KEYWORD_NOT_FOUND(HttpStatus.NOT_FOUND, "KEYWORD404-NOT_FOUND", "존재하지 않거나 유효하지 않은 키워드가 요청되었습니다."),
    _KEYWORD_SELECTION_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "KEYWORD400-SELECTION_LIMIT_EXCEEDED", "키워드 선택 최대 개수를 초과했습니다."),
    _KEYWORD_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "KEYWORD400-INVALID_REQUEST", "키워드 요청 데이터가 올바르지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
