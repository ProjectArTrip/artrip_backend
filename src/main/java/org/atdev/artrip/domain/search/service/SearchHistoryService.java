package org.atdev.artrip.domain.search.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.domain.auth.data.User;
import org.atdev.artrip.domain.auth.repository.UserRepository;
import org.atdev.artrip.domain.search.data.SearchHistory;
import org.atdev.artrip.domain.search.repository.SearchHistoryRepository;
import org.atdev.artrip.global.apipayload.code.status.CommonError;
import org.atdev.artrip.global.apipayload.code.status.SearchError;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;

    private static final String POPULAR_KEYWORDS_KEY = "search:popular_keywords";

    @PostConstruct
    public void initRedis() {
        try {
            log.info("Redis cache with popular keywords");
            List<String> keywords = searchHistoryRepository.findPopularKeywords();

            if (!keywords.isEmpty()) {
                redisTemplate.delete(POPULAR_KEYWORDS_KEY);
                syncToRedis(keywords);
            }
        } catch (Exception e) {
            log.error("redis cache filed : {}", e.getMessage(), e);
        }
    }

    @Scheduled(fixedRate = 300000)
    public void syncToRedis() {
        try {
            List<String> keywords = searchHistoryRepository.findPopularKeywords();

            if (!keywords.isEmpty()) {
                redisTemplate.delete(POPULAR_KEYWORDS_KEY);
                syncToRedis(keywords);
            }
        } catch (Exception e) {
            log.error("redis cache filed : {}", e.getMessage(), e);
        }
    }

    @Transactional
    public void create(Long userId, String keyword) {
        //TODO: 배포 전 로그 레벨 조정
        try {
            log.debug("Saving search history for userId: {}, keyword: {}", userId, keyword);

            User user = userRepository.findById(userId).orElseThrow(() -> new GeneralException(CommonError._INTERNAL_SERVER_ERROR));

            SearchHistory history = SearchHistory.builder()
                    .user(user)
                    .content(keyword)
                    .createdAt(LocalDateTime.now())
                    .build();
            searchHistoryRepository.save(history);

        redisTemplate.opsForZSet().incrementScore(POPULAR_KEYWORDS_KEY, keyword, 1);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public List<String> findRecent(Long userId) {
        //TODO: 배포 전 로그 레벨 조정
        log.info("Searching for userId: {}", userId);
        return searchHistoryRepository.findTop10ByUser_UserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(SearchHistory::getContent)
                .distinct()
                .toList();
    }

    @Transactional
    public void remove(Long userId, String keyword) {
        try {
            log.debug("Deleting search history for userId: {}, keyword: {}", userId, keyword);
            searchHistoryRepository.deleteByUserIdAndContent(userId, keyword);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GeneralException(SearchError._SEARCH_HISTORY_NOT_FOUND);
        }
    }

    @Transactional
    public void removeAll(Long userId) {
        try {
            log.debug("Deleting all search history for userId: {}", userId);
            searchHistoryRepository.deleteByUserId(userId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GeneralException(SearchError._SEARCH_EXHIBIT_NOT_FOUND);
        }
    }

    public List<String> findPopularKeywords() {
        try {
            Set<String> redisResult = redisTemplate.opsForZSet()
                    .reverseRange(POPULAR_KEYWORDS_KEY, 0, 4);

            if (redisResult != null && !redisResult.isEmpty()) {
                log.debug("Popular keywords from Redis: {}", redisResult);
                return redisResult.stream().toList();
            }
            log.debug("Redis empty, falling back to MySQL");
            List<String> mysqlResult = searchHistoryRepository.findPopularKeywords();

            if (!mysqlResult.isEmpty()) {
                syncToRedis(mysqlResult);
            }

            return mysqlResult;
        } catch (Exception e) {
            log.error("Error finding popular keywrods from Redis: {}", e.getMessage(), e );
            return searchHistoryRepository.findPopularKeywords();
        }
    }

    private void syncToRedis(List<String> keywords) {
        try {
            for (int i = 0; i < keywords.size(); i++) {
                double score = keywords.size() - i;
                redisTemplate.opsForZSet().add(POPULAR_KEYWORDS_KEY, keywords.get(i), score);
            }
            log.debug("Synced {} keywords to Redis", keywords.size());
        } catch (Exception e) {
            log.error("error syncing to Redis: {}", e.getMessage(), e);
        }
    }

}
