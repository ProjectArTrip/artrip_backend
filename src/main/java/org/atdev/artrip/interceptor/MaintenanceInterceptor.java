package org.atdev.artrip.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.config.MaintenanceAccessProperties;
import org.atdev.artrip.constants.MaintenanceState;
import org.atdev.artrip.controller.dto.response.MaintenanceStatusResponse;
import org.atdev.artrip.service.MaintenanceService;
import org.atdev.artrip.service.dto.result.MaintenanceStatusResult;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MaintenanceInterceptor implements HandlerInterceptor {

    private final MaintenanceService maintenanceService;
    private final MaintenanceAccessProperties maintenanceAccessProperties;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        MaintenanceStatusResult maintenanceStatus = maintenanceService.getMaintenanceStatus();

        if (!maintenanceStatus.active() || maintenanceStatus.state() != MaintenanceState.BLOCK) {
            return true;
        }

        String requestUri = request.getRequestURI();
        if ("/maintenance".equals(requestUri)) {
            return true;
        }

        if (DispatcherType.ERROR == request.getDispatcherType()) {
            return true;
        }

        List<String> allowedPaths = maintenanceAccessProperties.allowedPaths();

        if (allowedPaths != null) {
            for (String allowedPath : allowedPaths) {
                if (antPathMatcher.match(allowedPath, requestUri)) {
                    return true;
                }
            }
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            boolean isAdmin = authentication.getAuthorities().stream().anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));

            if (isAdmin) {
                return true;
            }
        }

        String clientIp = request.getRemoteAddr();

        List<String> allowedIps = maintenanceAccessProperties.allowedIps();
        if (allowedIps != null && allowedIps.contains(clientIp)) {
            return true;
        }


        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), MaintenanceStatusResponse.from(maintenanceStatus));

        return false;
    }
}
