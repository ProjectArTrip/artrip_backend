package org.atdev.artrip.service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserHistoryRedisService {

    @Qualifier("recommendRedisTemplate")
    private final StringRedisTemplate recommendRedisTemplate;

    private static final String KEY_PREFIX = "user:history:";

    @Async
    public void addRecentView(Long userId, Long exhibitId) {
        String key = KEY_PREFIX + userId;
        double now = System.currentTimeMillis();

        recommendRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            byte[] rawKey = key.getBytes();
            byte[] rawValue = String.valueOf(exhibitId).getBytes();

            connection.zAdd(rawKey, now, rawValue);
            connection.zRemRange(rawKey, 0, -21);
            connection.expire(rawKey, 2592000);
            return null;
        });
    }
}