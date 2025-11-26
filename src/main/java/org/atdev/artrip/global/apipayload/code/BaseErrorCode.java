package org.atdev.artrip.global.apipayload.code;

import org.springframework.http.HttpStatus;

public interface BaseErrorCode {

    HttpStatus getHttpStatus();
    String getCode();
    String getMessage();

    default ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(getMessage())
                .code(getCode())
                .isSuccess(false)
                .build();
    }

    default ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(getMessage())
                .code(getCode())
                .isSuccess(false)
                .httpStatus(getHttpStatus())
                .build();
    }

}