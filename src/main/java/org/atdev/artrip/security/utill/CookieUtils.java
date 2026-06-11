package org.atdev.artrip.security.utill;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.config.CookieProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class CookieUtils {

    private static final String ACCESS = "accessToken";
    private static final String REFRESH = "refreshToken";

    private final CookieProperties cookieProperties;

    public void writeAccessToken(HttpServletResponse response, String token, Duration maxAge) {
        response.addHeader(HttpHeaders.SET_COOKIE, build(ACCESS, token, maxAge).toString());
    }

    public void writeRefreshToken(HttpServletResponse response, String token, Duration maxAge) {
        response.addHeader(HttpHeaders.SET_COOKIE, build(REFRESH, token, maxAge).toString());
    }

    public void expireAuthCookies(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, build(ACCESS, "", Duration.ZERO).toString());
        response.addHeader(HttpHeaders.SET_COOKIE, build(REFRESH, "", Duration.ZERO).toString());

    }

    private ResponseCookie build(String name, String value, Duration maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(cookieProperties.isSecure())
                .sameSite(cookieProperties.getSameSite())
                .path("/")
                .maxAge(maxAge)
                .build();
    }
}
