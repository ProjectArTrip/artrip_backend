package org.atdev.artrip.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.atdev.artrip.constants.Role;
import org.atdev.artrip.domain.auth.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Calendar;
import java.util.Date;

@Component
public class JwtGenerator {
    private final Key key;
    private static final String GRANT_TYPE = "Bearer";

    @Value("${spring.jwt.issuer}")
    private String jwtIssuer;

    @Value("${spring.jwt.access-token-expiration-millis}")
    private long accessTokenExpirationMillis;

    @Value("${spring.jwt.refresh-token-expiration-millis}")
    private long refreshTokenExpirationMillis;

    public JwtGenerator(Key key) {
        this.key = key;
    }

    public JwtToken generateToken(User user, Role roles) {

        long now = (new Date()).getTime();

        String authorities = "ROLE_" + roles.name();

        String accessToken = Jwts.builder()
                .setIssuer(jwtIssuer)
                .setSubject(String.valueOf(user.getUserId()))//userid로 할경우 jwt는 사양상 String 타입을 요구함 따라서string변환
                .claim("auth", authorities)// 권한 설정
                .setExpiration(new Date(now + accessTokenExpirationMillis))
                .setIssuedAt(Calendar.getInstance().getTime())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + refreshTokenExpirationMillis))//7일 만료
                .setIssuedAt(Calendar.getInstance().getTime())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return JwtToken.builder()
                .grantType(GRANT_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String createAccessToken(String subject, String roles) {// refresh 재발행때 사용
        long now = System.currentTimeMillis();

//        String authorities = "Role_" + roles;

        return Jwts.builder()
                .setIssuer(jwtIssuer)
                .setSubject(subject)
                .claim("auth", roles)
                .setExpiration(new Date(now + accessTokenExpirationMillis))
                .setIssuedAt(new Date(now))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
