package org.atdev.artrip.jwt.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRedisRepository {

    private final StringRedisTemplate jwtRedisTemplate;

    public void save(String key, String userid, long expirationMillis) {
        jwtRedisTemplate.opsForValue().set(
                key,
                userid,
                Duration.ofMillis(expirationMillis)
        );
    }

    public String findByUsername(String key) {
        return jwtRedisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        jwtRedisTemplate.delete(key);
    }
}
