package org.atdev.artrip.jwt;


import io.jsonwebtoken.*;
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

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;

@Slf4j
@Component
public class JwtProvider {

    private final Key key;

    public JwtProvider(Key key) {
        this.key = key;
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null || claims.get("auth").toString().isEmpty()) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .toList();

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("Invalid JWT Token", e);
            throw new JwtAuthenticationException(UserError._JWT_INVALID_SIGNATURE);
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT Token", e);
            throw new JwtAuthenticationException(UserError._JWT_EXPIRED_ACCESS_TOKEN);
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT Token", e);
            throw new JwtAuthenticationException(UserError._JWT_UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty.", e);
            throw new JwtAuthenticationException(UserError._JWT_INVALID_TOKEN);
        }
    }
    public void validateRefreshToken(String refreshToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(refreshToken);
        } catch (ExpiredJwtException e) {
            log.warn("Expired Refresh Token", e);
            throw new GeneralException(UserError._JWT_EXPIRED_REFRESH_TOKEN);
        } catch (JwtException e) {
            log.warn("Invalid Refresh Token", e);
            throw new GeneralException(UserError._INVALID_REFRESH_TOKEN);
        }
    }
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }




}


