package org.atdev.artrip.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Object ts = request.getAttribute(START_TIME);
        if (ts == null) return;

        long elapsed = System.currentTimeMillis() - (long) ts;
        int status = response.getStatus();
        String msg = "[{}] {} -> {} ({}ms)";

        if (status >= 500) {
            log.error(msg, request.getMethod(), request.getRequestURI(), status, elapsed);
        } else if (status >= 400) {
            log.warn(msg, request.getMethod(), request.getRequestURI(), status, elapsed);
        } else {
            log.info(msg, request.getMethod(), request.getRequestURI(), status, elapsed);
        }
    }
}
