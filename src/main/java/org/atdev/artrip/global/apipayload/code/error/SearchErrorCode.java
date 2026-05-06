package org.atdev.artrip.global.apipayload.code.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.atdev.artrip.global.apipayload.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SearchErrorCode implements BaseErrorCode {

    _SEARCH_HISTORY_CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "SEARCH-400_BADREQUEST", "검색어는 10자 이하로 입력해주세요."),
    _SEARCH_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "SEARCH-404_NOTFOUND", "검색 기록을 찾을 수 없습니다."),
    _SEARCH_HISTORY_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN, "SEARCH-403_FORBIDDEN", "해당 검색 기록을 삭제할 권한이 없습니다."),
    _SEARCH_HISTORY_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SEARCH_500", "검색어 저장에 실패했습니다."),
    _SEARCH_HISTORY_DUPLICATED(HttpStatus.CONFLICT, "SEARCH-409_SEARCH_DUPLICATED", "이미 저장된 검색어 입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
