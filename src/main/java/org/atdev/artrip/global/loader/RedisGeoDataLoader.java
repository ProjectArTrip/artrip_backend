package org.atdev.artrip.global.loader;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.repository.ExhibitRepository;
import org.atdev.artrip.repository.dto.ExhibitMarkerDto;
import org.atdev.artrip.service.redis.RedisService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisGeoDataLoader {

    private final ExhibitRepository exhibitRepository;
    private final RedisService redisService;
    private static final String REDIS_KEY = "exhibits:locations";

    @EventListener(ApplicationReadyEvent.class)
    @Transactional(readOnly = true)
    public void loadDBToRedisGeo() {
        log.info(">>> Redis Geo 캐시 로딩 시작");

        if (redisService.hasKey("exhibits:locations")) {
            log.info(">>> 이미 Redis 캐시가 존재하여 로딩을 스킵합니다.");
            return;
        }

        List<ExhibitMarkerDto> locations = exhibitRepository.findAllLocationsForCache();
        if (locations.isEmpty()) {
            log.warn(">>> 로드할 데이터가 DB에 없습니다.");
            return;
        }

        List<GeoLocation<String>> geoBatch = locations.stream()
                .filter(this::hasCoordinates)
                .map(dto -> new GeoLocation<>(
                        dto.exhibitId().toString(),
                        new Point(dto.longitude().doubleValue(), dto.latitude().doubleValue())
                ))
                .collect(Collectors.toList());

        if (!geoBatch.isEmpty()) {
            redisService.saveGeoLocationBulk(REDIS_KEY, geoBatch);
            log.info(">>> 총 {}건의 위치 데이터 로드 완료.", geoBatch.size());
        }
    }

    private boolean hasCoordinates(ExhibitMarkerDto dto) {
        return dto.longitude() != null && dto.latitude() != null;
    }
}