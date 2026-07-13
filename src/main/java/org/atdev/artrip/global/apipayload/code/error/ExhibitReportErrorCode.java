package org.atdev.artrip.global.apipayload.code.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.atdev.artrip.global.apipayload.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExhibitReportErrorCode implements BaseErrorCode {

    _EXHIBIT_REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "EXHIBITREPORT404-NOT_FOUND", "전시 제보 정보를 찾을 수 없습니다."),
    _EXHIBIT_REPORT_ALREADY_REGISTERED(HttpStatus.CONFLICT, "EXHIBITREPORT409-ALREADY_REGISTERED", "이미 등록 처리된 제보입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
