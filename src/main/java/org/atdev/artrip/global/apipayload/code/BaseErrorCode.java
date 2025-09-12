package org.atdev.artrip.global.apipayload.code;

public interface BaseErrorCode {

    ErrorReasonDTO getReason();
    ErrorReasonDTO  getReasonHttpStatus();
}