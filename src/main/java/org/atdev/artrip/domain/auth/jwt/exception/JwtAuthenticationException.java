package org.atdev.artrip.domain.auth.jwt.exception;

import lombok.Getter;
import org.atdev.artrip.global.apipayload.code.status.UserError;

@Getter
public class JwtAuthenticationException extends RuntimeException {

    private final UserError userError;

    public JwtAuthenticationException(UserError userError) {
        super(userError.getMessage());
        this.userError = userError;
    }
}
