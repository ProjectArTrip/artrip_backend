package org.atdev.artrip.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    @DisplayName("Redis 서버 연결 테스트")
    void redisPingTest() {
        //given
        String key = "test:key";
        String value = "redis-hi";

        // when
        redisTemplate.opsForValue().set(key, value);
        String findValue = redisTemplate.opsForValue().get(key);

        // then
        assertThat(findValue).isEqualTo(value);
    }

}
