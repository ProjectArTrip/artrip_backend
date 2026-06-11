package org.atdev.artrip.global.apipayload.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.global.apipayload.code.ErrorReasonDTO;
import org.atdev.artrip.global.apipayload.code.error.UserErrorCode;
import org.atdev.artrip.jwt.exception.JwtAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        UserErrorCode errorCode;

        Object stored = request.getAttribute("exception");
        if (stored instanceof JwtAuthenticationException jwtEx) {
            errorCode = jwtEx.getUserErrorCode();
        } else {
            errorCode = UserErrorCode._JWT_EMPTY_TOKEN;
        }

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        ErrorReasonDTO errorResponse = errorCode.getReasonHttpStatus();

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
