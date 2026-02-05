package org.atdev.artrip.global.apipayload.code;

import org.springframework.http.HttpStatus;

public interface BaseCode {

    HttpStatus getHttpStatus();
    String getCode();
    String getMessage();

    default ReasonDTO getReason() {
        return ReasonDTO.builder()
                .message(getMessage())
                .code(getCode())
                .build();
    }

    default ReasonDTO getReasonHttpStatus() {
        return ReasonDTO.builder()
                .message(getMessage())
                .code(getCode())
                .httpStatus(getHttpStatus())
                .build();
    }



}