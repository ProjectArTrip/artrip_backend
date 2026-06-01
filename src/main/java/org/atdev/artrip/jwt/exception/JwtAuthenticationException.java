package org.atdev.artrip.jwt.exception;

import lombok.Getter;
import org.atdev.artrip.global.apipayload.code.error.UserErrorCode;
import org.springframework.security.core.AuthenticationException;

@Getter
public class JwtAuthenticationException extends AuthenticationException {

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
