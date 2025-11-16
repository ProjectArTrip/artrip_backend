package org.atdev.artrip.domain.auth.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.auth.data.User;
import org.atdev.artrip.domain.auth.jwt.JwtGenerator;
import org.atdev.artrip.domain.auth.jwt.JwtProvider;
import org.atdev.artrip.domain.auth.jwt.exception.JwtAuthenticationException;
import org.atdev.artrip.domain.auth.jwt.repository.RefreshTokenRedisRepository;
import org.atdev.artrip.domain.auth.repository.UserRepository;
import org.atdev.artrip.global.apipayload.code.status.ErrorStatus;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final JwtGenerator jwtGenerator;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    public String reissueToken(String refreshToken, HttpServletResponse response) {
        if (refreshToken == null) {
            throw new GeneralException(ErrorStatus._INVALID_REFRESH_TOKEN);
        }

        jwtProvider.validateRefreshToken(refreshToken);

        String userId = redisTemplate.opsForValue().get(refreshToken);

        if (userId == null) {
            throw new GeneralException(ErrorStatus._INVALID_REFRESH_TOKEN);
        }

        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));

        String newAccessToken = jwtGenerator.createAccessToken(user.getUserId().toString(), user.getRole().name());

        Cookie accessCookie = new Cookie("accessToken", newAccessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(false);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(15 * 60);

        response.addCookie(accessCookie);

        return newAccessToken;
    }

    public void logout(String refreshToken, HttpServletResponse response) {

        if(refreshToken != null) {
            refreshTokenRedisRepository.delete(refreshToken);
        }

        expireCookie("accessToken", response);
        expireCookie("refreshToken", response);
    }

    private void expireCookie(String name, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 즉시 만료
        response.addCookie(cookie);
    }

}
