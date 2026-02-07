package org.atdev.artrip.service.redis;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

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

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void saveGeoLocationBulk(String key, List<GeoLocation<String>> locations) {
        if (locations != null && !locations.isEmpty()) {
            redisTemplate.opsForGeo().add(key, locations);
        }
    }
    /**
     * 반경 내 멤버 조회 (나중에 주변 전시회 찾을 때 사용)
     */
//    public GeoResults<RedisGeoCommands.GeoLocation<String>> getNearby(String key, double lon, double lat, double distanceKm) {
//        Circle circle = new Circle(new Point(lon, lat), new Distance(distanceKm, Metrics.KILOMETERS));
//        return redisTemplate.opsForGeo().radius(key, circle);
//    }

}