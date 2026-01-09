package org.atdev.artrip.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    public void save(String key, String value, long durationMillis) {
        redisTemplate.opsForValue().set(key, value, Duration.ofMillis(durationMillis));
    }

    public void saveBlacklist(String accessToken, long expirationMillis) {
        save("BLACKLIST:" + accessToken, "logout", expirationMillis);
    }

    public boolean isBlacklisted(String accessToken) {
        return getValue("BLACKLIST:" + accessToken) != null;
    }
}
