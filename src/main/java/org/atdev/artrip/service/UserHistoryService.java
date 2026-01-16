package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.repository.UserHistoryRedisRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserHistoryService {

    private final UserHistoryRedisRepository userHistoryRepository;

    private static final int RECENT_VIEW_LIMIT = 20;
    private static final long RECENT_VIEW_TTL_SECONDS = 2592000L;
    private static final String KEY_PREFIX = "user:history:";

    @Async("redisThreadPoolExecutor")
    public void addRecentView(Long userId, Long exhibitId) {
        userHistoryRepository.saveWithLimit(
                KEY_PREFIX + userId,
                String.valueOf(exhibitId),
                System.currentTimeMillis(),
                RECENT_VIEW_LIMIT,
                RECENT_VIEW_TTL_SECONDS
        );
    }
}