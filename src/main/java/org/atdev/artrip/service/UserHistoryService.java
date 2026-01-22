package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.repository.UserHistoryRedisRepository;
import org.atdev.artrip.utils.RedisUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserHistoryService {

    private final UserHistoryRedisRepository userHistoryRepository;

    private static final int RECENT_VIEW_LIMIT = 20;
    private static final long RECENT_VIEW_TTL_SECONDS = 2592000L;

    @Async("redisThreadPoolExecutor")
    public void addRecentView(Long userId, Long exhibitId) {
        String key = RedisUtils.getRecentViewKey(userId);

        userHistoryRepository.saveWithLimit(
                key,
                String.valueOf(exhibitId),
                System.currentTimeMillis(),
                RECENT_VIEW_LIMIT,
                RECENT_VIEW_TTL_SECONDS
        );
    }
}