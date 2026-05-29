package org.atdev.artrip.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.constants.Role;
import org.atdev.artrip.domain.auth.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtGenerator {
    private final Key key;
    private static final String GRANT_TYPE = "Bearer";
    private static final String ROLE_PREFIX = "ROLE_";
    private static final String AUTHORITIES_KEY = "auth";
    private static final String TYPE_KEY = "type";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    @Value("${spring.jwt.issuer}")
    private String jwtIssuer;

    @Value("${spring.jwt.access-token-expiration-millis}")
    private long accessTokenExpirationMillis;

    @Value("${spring.jwt.refresh-token-expiration-millis}")
    private long refreshTokenExpirationMillis;

    public JwtToken generateToken(User user, Role role) {

        long now = (new Date()).getTime();
        String authority = ROLE_PREFIX + role.name();

        String accessToken = Jwts.builder()
                .setIssuer(jwtIssuer)
                .setSubject(user.getUserIdAsString())
                .claim(AUTHORITIES_KEY, authority)
                .claim(TYPE_KEY, TYPE_ACCESS)
                .setExpiration(new Date(now + accessTokenExpirationMillis))
                .setIssuedAt(new Date(now))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setIssuer(jwtIssuer)
                .setSubject(user.getUserIdAsString())
                .claim(TYPE_KEY, TYPE_REFRESH)
                .setExpiration(new Date(now + refreshTokenExpirationMillis))
                .setIssuedAt(new Date(now))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return JwtToken.builder()
                .grantType(GRANT_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String createAccessToken(User user, Role role) {

        long now = System.currentTimeMillis();
        String authority = ROLE_PREFIX + role.name();

        return Jwts.builder()
                .setIssuer(jwtIssuer)
                .setSubject(user.getUserIdAsString())
                .claim(AUTHORITIES_KEY, authority)
                .claim(TYPE_KEY, TYPE_ACCESS)
                .setExpiration(new Date(now + accessTokenExpirationMillis))
                .setIssuedAt(new Date(now))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
