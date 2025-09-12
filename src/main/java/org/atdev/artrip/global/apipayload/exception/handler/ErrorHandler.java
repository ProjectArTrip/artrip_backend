package org.atdev.artrip.global.apipayload.exception.handler;

import org.atdev.artrip.global.apipayload.code.BaseErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;

public class ErrorHandler extends GeneralException {
    public ErrorHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}