package org.atdev.artrip.config;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.jsonwebtoken.JwtParser;

import java.security.Key;

@Configuration
public class JwtConfig {

    @Bean
    public Key jwtSigningKey(@Value("${spring.jwt.secret}") String secret) {
        byte[] decoded = Decoders.BASE64.decode(secret);

        if (decoded.length < 32) {
            throw new IllegalStateException("JWT secret must be at least 32 bytes long, but was " + decoded.length);
        }

        return Keys.hmacShaKeyFor(decoded);
    }

    @Bean
    public JwtParser jwtParser(Key key, @Value("${spring.jwt.issuer}") String issuer) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .requireIssuer(issuer)
                .setAllowedClockSkewSeconds(30)
                .build();
    }

}
