package org.atdev.artrip.global.apipayload.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.atdev.artrip.global.apipayload.code.BaseErrorCode;
import org.atdev.artrip.global.apipayload.code.ErrorReasonDTO;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

    private BaseErrorCode code;

    public ErrorReasonDTO getErrorReason() {
        return this.code.getReason();
    }

    public ErrorReasonDTO getErrorReasonHttpStatus(){
        return this.code.getReasonHttpStatus();
    }
}