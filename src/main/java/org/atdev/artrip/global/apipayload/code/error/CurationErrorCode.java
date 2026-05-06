package org.atdev.artrip.global.apipayload.code.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.atdev.artrip.global.apipayload.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CurationErrorCode implements BaseErrorCode {

    _CURATION_NOT_FOUND(HttpStatus.NOT_FOUND, "CURATION404-NOT_FOUND", "큐레이션을 찾을 수 없습니다."),
    _CURATION_NOT_VISIBLE(HttpStatus.BAD_REQUEST, "CURATION400-NOT_VISIBLE", "현재 노출되지 않는 큐레이션입니다."),
    _INVALID_COUNTRY(HttpStatus.BAD_REQUEST, "CURATION400-INVALID_COUNTRY", "허용된 국가가 아닙니다."),
    _INVALID_LOCATION_FILTER(HttpStatus.BAD_REQUEST, "CURATION400-LOCATION_FILTER", "국가 및 지역 조합이 맞지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
