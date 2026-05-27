package org.atdev.artrip.global.apipayload.code.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.atdev.artrip.global.apipayload.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExhibitErrorCode implements BaseErrorCode {

    _EXHIBIT_NOT_FOUND(HttpStatus.NOT_FOUND, "EXHIBIT404-NOT_FOUND", "전시 정보를 찾을 수 없습니다."),
    _EXHIBIT_HALL_NOT_FOUND(HttpStatus.NOT_FOUND, "EXHIBITHALL404-NOT_FOUND", "전시관 정보를 찾을 수 없습니다."),
    _EXHIBIT_HALL_IN_USE(HttpStatus.BAD_REQUEST, "EXHIBITHALL400-IN_USE", "전시관이 전시와 연관되어 있어 삭제할 수 없습니다."),

    _EXHIBIT_INVALID_STATUS(HttpStatus.BAD_REQUEST, "EXHIBIT400-INVALID_STATUS", "유효하지 않은 전시 상태입니다."),
    _EXHIBIT_INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "EXHIBIT400-INVALID_DATE_RANGE", "전시 시작일이 종료일보다 늦을 수 없습니다."),
    _EXHIBIT_KEYWORD_NOT_FOUND(HttpStatus.BAD_REQUEST, "EXHIBIT400-KEYWORD_NOT_FOUND", "존재하지 않는 장르가 포함되어 있습니다."),

    _CSV_EMPTY(HttpStatus.BAD_REQUEST, "EXHIBIT400-CSV_EMPTY", "CSV 파일이 비어 있습니다."),
    _CSV_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "EXHIBIT400-CSV_INVALID_FORMAT", "CSV 형식이 올바르지 않습니다."),
    _CSV_INVALID_ROW(HttpStatus.BAD_REQUEST, "EXHIBIT400-CSV_INVALID_ROW", "잘못된 CSV 행이 포함되어 있습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
