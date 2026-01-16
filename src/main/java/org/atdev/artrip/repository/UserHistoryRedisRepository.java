package org.atdev.artrip.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserHistoryRedisRepository {
    private final StringRedisTemplate recommendRedisTemplate;

    public void saveWithLimit(String key, String value, double score, int limit, long ttl) {
        recommendRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            connection.zAdd(key.getBytes(), score, value.getBytes());
            connection.zRemRange(key.getBytes(), 0, -(limit + 1));
            connection.expire(key.getBytes(), ttl);
            return null;
        });
    }
}