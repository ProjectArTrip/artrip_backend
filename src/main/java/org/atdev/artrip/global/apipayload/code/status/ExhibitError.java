package org.atdev.artrip.global.apipayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.atdev.artrip.global.apipayload.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExhibitError implements BaseErrorCode {

    _EXHIBIT_NOT_FOUND(HttpStatus.NOT_FOUND, "EXHIBIT404-NOT_FOUND", "전시 정보를 찾을 수 없습니다."),
    _EXHIBIT_HALL_NOT_FOUND(HttpStatus.NOT_FOUND, "EXHIBITHALL404-NOT_FOUND", "전시관 정보를 찾을 수 없습니다."),
    _EXHIBIT_HALL_IN_USE(HttpStatus.BAD_REQUEST, "EXHIBITHALL400-IN_USE", "전시관이 전시와 연관되어 있어 삭제할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
