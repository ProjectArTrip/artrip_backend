package org.atdev.artrip.global.apipayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.atdev.artrip.global.apipayload.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SearchErrorCode implements BaseErrorCode {


    _SEARCH_KEYWORD_INVALID(HttpStatus.BAD_REQUEST, "SEARCH400-KEYWORD_INVALID", "검색 키워드가 유효하지 않습니다."),
    _SEARCH_KEYWORD_TOO_SHORT(HttpStatus.BAD_REQUEST, "SEARCH400-KEYWORD_TOO_SHORT", "검색 키워드가 너무 짧습니다. (최소 2자 이상)"),
    _SEARCH_KEYWORD_TOO_LONG(HttpStatus.BAD_REQUEST, "SEARCH400-KEYWORD_TOO_LONG", "검색 키워드가 너무 깁니다. (최대 100자 이하)"),
    _SEARCH_KEYWORD_EMPTY(HttpStatus.BAD_REQUEST, "SEARCH400-KEYWORD_EMPTY", "검색 키워드를 입력해주세요."),
    _SEARCH_KEYWORD_CONTAINS_SPECIAL_CHAR(HttpStatus.BAD_REQUEST, "SEARCH400-KEYWORD_SPECIAL_CHAR", "검색 키워드에 허용되지 않는 특수문자가 포함되어 있습니다."),
    _SEARCH_INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "SEARCH400-INVALID_PARAM", "잘못된 검색 파라미터입니다."),
    _SEARCH_RECOMMENDATION_NOT_FOUND(HttpStatus.NOT_FOUND, "SEARCH404-ES_RECOMMEND_NOT_FOUND", "추천 검색 결과를 찾을 수 없습니다."),

    _SEARCH_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "SEARCH401-UNAUTHORIZED", "검색을 위해 로그인이 필요합니다."),

    _SEARCH_EXHIBIT_NOT_FOUND(HttpStatus.NOT_FOUND, "SEARCH404-EXHIBIT_NOT_FOUND", "검색 결과가 없습니다."),
    _SEARCH_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "SEARCH404-HISTORY_NOT_FOUND", "삭제할 검색 기록을 찾을 수 없습니다."),
    _SEARCH_INDEX_NOT_FOUND(HttpStatus.NOT_FOUND, "SEARCH404-INDEX_NOT_FOUND", "검색 인덱스를 찾을 수 없습니다."),

    _SEARCH_TOO_FREQUENT(HttpStatus.TOO_MANY_REQUESTS, "SEARCH429-TOO_FREQUENT", "검색 요청이 너무 빈번합니다. 잠시 후 다시 시도해주세요."),
    _SEARCH_RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "SEARCH429-RATE_LIMIT", "검색 요청 제한을 초과했습니다."),

    _SEARCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SEARCH500-FAILED", "검색 중 오류가 발생했습니다."),
    _SEARCH_QUERY_EXECUTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SEARCH500-QUERY_FAILED", "검색 쿼리 실행에 실패했습니다."),
    _SEARCH_RESULT_PARSE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SEARCH500-PARSE_FAILED", "검색 결과 처리 중 오류가 발생했습니다."),
    _SEARCH_HISTORY_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SEARCH500-HISTORY_SAVE_FAILED", "검색 기록 저장에 실패했습니다."),
    _SEARCH_HISTORY_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SEARCH500-HISTORY_DELETE_FAILED", "검색 기록 삭제에 실패했습니다."),
    _SEARCH_PERSONALIZED_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SEARCH500-PERSONALIZED_FAILED", "취향 맞춤 검색 중 오류가 발생했습니다."),

    _SEARCH_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "SEARCH503-UNAVAILABLE", "검색 서비스를 일시적으로 사용할 수 없습니다."),
    _SEARCH_ES_CONNECTION_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "SEARCH503-ES_CONNECTION", "검색 서버에 연결할 수 없습니다."),
    _SEARCH_ES_TIMEOUT(HttpStatus.SERVICE_UNAVAILABLE, "SEARCH503-ES_TIMEOUT", "검색 서버 응답 시간이 초과되었습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
