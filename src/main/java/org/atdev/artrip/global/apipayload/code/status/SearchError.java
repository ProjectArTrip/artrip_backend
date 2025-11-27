package org.atdev.artrip.global.apipayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.atdev.artrip.global.apipayload.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SearchError implements BaseErrorCode {

    _SEARCH_EXHIBIT_NOT_FOUND(HttpStatus.NOT_FOUND, "SEARCH401", "요청하신 키워드에 해당하는 전시 결과를 찾을 수 없습니다."),
    _SEARCH_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "SEARCH402", "삭제를 요청한 검색 기록을 찾을 수 없습니다."),

    _SEARCH_KEYWORD_INVALID(HttpStatus.BAD_REQUEST, "SEARCH401", "검색 키워드가 너무 짧거나 (최소 2자 이상), 부적절한 문자열을 포함하고 있습니다."),
    _SEARCH_TOO_FREQUENT(HttpStatus.BAD_REQUEST, "SEARCH402", "검색 요청이 너무 빈번합니다. 잠시 후 다시 시도해 주세요."); // Rate Limit 성격


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
