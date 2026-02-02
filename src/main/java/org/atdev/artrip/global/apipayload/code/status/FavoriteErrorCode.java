package org.atdev.artrip.global.apipayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.atdev.artrip.global.apipayload.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FavoriteErrorCode implements BaseErrorCode {

    _FAVORITE_NOT_FOUND(HttpStatus.NOT_FOUND, "FAVORITE404-NOT_FOUND", "즐겨찾기를 찾을 수 없습니다."),
    _INVALID_SORT_TYPE(HttpStatus.BAD_REQUEST, "FAVORITE400-INVALID_SORT", "유효하지 않은 정렬 타입입니다. (latest, ending_soon만 가능)"),
    _REGION_REQUIRES_DOMESTIC(HttpStatus.BAD_REQUEST, "FAVORITE400-REGION_REQ_DOMESTIC", "지역 필터는 국내 전시(isDomestic=true)일 때만 사용 가능합니다."),
    _COUNTRY_REQUIRES_OVERSEAS(HttpStatus.BAD_REQUEST, "FAVORITE400-COUNTRY_REQ_OVERSEAS", "국가 필터는 해외 전시(isDomestic=false)일 때만 사용 가능합니다."),
    _REQUIRES_DOMESTIC(HttpStatus.BAD_REQUEST, "FAVORITE400-DOMESTIC_FILTER", "지역 및 국사 필터는 isDomestic 입력값 필수 입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
