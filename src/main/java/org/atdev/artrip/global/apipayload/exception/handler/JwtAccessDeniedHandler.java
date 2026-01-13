package org.atdev.artrip.global.apipayload.exception.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.global.apipayload.code.ErrorReasonDTO;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {

        UserErrorCode errorCode = UserErrorCode._USER_FORBIDDEN;

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        ErrorReasonDTO errorResponse = errorCode.getReasonHttpStatus();

        response.getWriter().write(
                objectMapper.writeValueAsString(errorResponse)
        );
    }
}

