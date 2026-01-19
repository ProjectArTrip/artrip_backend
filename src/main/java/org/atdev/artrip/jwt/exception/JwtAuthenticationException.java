package org.atdev.artrip.jwt.exception;

import lombok.Getter;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;

@Getter
public class JwtAuthenticationException extends RuntimeException {

    private final UserErrorCode userErrorCode;

    public JwtAuthenticationException(UserErrorCode userErrorCode) {
        super(userErrorCode.getMessage());
        this.userErrorCode = userErrorCode;
    }
    public JwtAuthenticationException(UserErrorCode userErrorCode, Throwable e) {
        super(userErrorCode.getMessage(), e);
        this.userErrorCode = userErrorCode;
    }
}
