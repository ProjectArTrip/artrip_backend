package org.atdev.artrip.domain.auth.jwt.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRedisRepository {

    private final StringRedisTemplate redisTemplate;

    public void save(String key, String userid, long expirationMillis) {
        redisTemplate.opsForValue().set(
                key,
                userid,
                Duration.ofMillis(expirationMillis)
        );
    }

    public String findByUsername(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
