package org.atdev.artrip.auth.jwt;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.auth.jwt.exception.JwtAuthenticationException;
import org.springframework.beans.factory.annotation.Value;
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

    public JwtProvider(@Value("${spring.jwt.secret}") String secretKey) {
        log.debug("Secret key from application.yml: {}", secretKey);
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }
    public Key getKey() {
        return key;
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
            throw new JwtAuthenticationException("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT Token", e);
            throw new JwtAuthenticationException("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT Token", e);
            throw new JwtAuthenticationException("지원하지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty.", e);
            throw new JwtAuthenticationException("JWT 토큰이 잘못되었습니다.");
        }
        // return false;
    }
    public void validateRefreshToken(String refreshToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(refreshToken);
        } catch (ExpiredJwtException e) {
            log.warn("Expired Refresh Token", e);
            throw new JwtAuthenticationException("만료된 리프레시 토큰입니다.");
        } catch (JwtException e) {
            log.warn("Invalid Refresh Token", e);
            throw new JwtAuthenticationException("유효하지 않은 리프레시 토큰입니다.");
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


