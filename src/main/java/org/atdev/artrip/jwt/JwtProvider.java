package org.atdev.artrip.jwt;


import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.jwt.exception.JwtAuthenticationException;
import org.atdev.artrip.global.apipayload.code.status.UserError;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtParser parser;

    public Authentication getAuthentication(String accessToken) {

        Claims claims = parseClaims(accessToken);

        String subject = claims.getSubject();
        if (!StringUtils.hasText(subject)) {
            throw new JwtAuthenticationException(UserError._JWT_INVALID_CLAIMS);
        }

        Object auth = claims.get("auth");
        if (!(auth instanceof String authStr) || !StringUtils.hasText(authStr)) {
            throw new JwtAuthenticationException(UserError._JWT_INVALID_CLAIMS);
        }

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(authStr.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .map(SimpleGrantedAuthority::new)
                .toList();

        if (authorities.isEmpty()) {
            throw new JwtAuthenticationException(UserError._JWT_INVALID_CLAIMS);
        }

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    private Claims parseClaims(String token) {
        try {
            return parser.parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw new JwtAuthenticationException(UserError._JWT_EXPIRED_ACCESS_TOKEN,e);
        } catch (UnsupportedJwtException e){
            throw new JwtAuthenticationException(UserError._JWT_UNSUPPORTED_TOKEN,e);
        } catch (IllegalArgumentException e) {
            throw new JwtAuthenticationException(UserError._JWT_EMPTY_TOKEN,e);
        } catch (MalformedJwtException e) {
            throw new JwtAuthenticationException(UserError._JWT_MALFORMED_TOKEN,e);
        } catch (JwtException e) {
            throw new JwtAuthenticationException(UserError._JWT_INVALID_TOKEN,e);
        }
    }

    public void validateRefreshToken(String refreshToken) {
        try {
            parser.parseClaimsJws(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new GeneralException(UserError._JWT_EXPIRED_REFRESH_TOKEN,e);
        } catch (UnsupportedJwtException e){
            throw new GeneralException(UserError._JWT_UNSUPPORTED_TOKEN,e);
        } catch (IllegalArgumentException e) {
            throw new GeneralException(UserError._JWT_EMPTY_TOKEN,e);
        } catch (MalformedJwtException e) {
            throw new GeneralException(UserError._JWT_MALFORMED_TOKEN,e);
        } catch (JwtException e) {
            throw new GeneralException(UserError._INVALID_REFRESH_TOKEN,e);
        }
    }

    public long getExpiration(String accessToken) {

        try {
            Claims claims = parser.parseClaimsJws(accessToken).getBody();

            long expirationTime = claims.getExpiration().getTime();
            long now = new Date().getTime();

            return Math.max(0, expirationTime - now);
        } catch (ExpiredJwtException e) {
            return 0;
        } catch (Exception e) {
            log.warn("잘못된 토큰 로그아웃 시도가 감지 되었습니다");
            return 0;
        }
    }
}


