package org.atdev.artrip.global.apipayload.code.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.atdev.artrip.global.apipayload.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MaintenanceErrorCode implements BaseErrorCode {

    _MAINTENANCE_INVALID_PERIOD(HttpStatus.BAD_REQUEST, "MAINTENANCE400-INVALID_PERIOD", "점검 종료 시간은 시작 시간 이후여야 합니다."),
    _MAINTENANCE_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "MAINTENANCE400-INVALID_CONFIGURATION", "점검 설정값이 올바르지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
