package org.atdev.artrip.global.apipayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.atdev.artrip.global.apipayload.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum HomeError implements BaseErrorCode {

    _HOME_INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "HOME401-INVALID_DATE_RANGE", "전시 기간 설정이 올바르지 않습니다. 종료일이 시작일보다 빠릅니다."),
    _HOME_UNRECOGNIZED_REGION(HttpStatus.BAD_REQUEST, "HOME402-UNRECOGNIZED_REGION", "요청하신 국가 또는 지역 정보를 인식할 수 없습니다."),
    _HOME_EXHIBIT_NOT_FOUND(HttpStatus.BAD_REQUEST, "HOME404-EXHIBIT_NOT_FOUND", "해당 ID의 전시 상세 정보를 찾을 수 없습니다."),
    _HOME_GENRE_NOT_FOUND(HttpStatus.BAD_REQUEST, "HOME404-GENRE_NOT_FOUND", "요청하신 장르에 해당하는 전시 데이터가 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
