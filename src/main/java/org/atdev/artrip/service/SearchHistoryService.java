package org.atdev.artrip.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.domain.search.SearchHistory;
import org.atdev.artrip.repository.SearchHistoryRepository;
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
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;
    private final UserRepository userRepository;
    private final StringRedisTemplate recommendRedisTemplate;

    private static final String POPULAR_KEYWORDS_KEY = "search:popular_keywords";

    @PostConstruct
    public void initRedis() {
        try {
            List<String> keywords = searchHistoryRepository.findPopularKeywords();

            if (!keywords.isEmpty()) {
                recommendRedisTemplate.delete(POPULAR_KEYWORDS_KEY);
                syncToRedis(keywords);
            }
        } catch (Exception e) {
            System.out.println( "init Redis : " + e.getMessage());
        }
    }

    @Scheduled(fixedRate = 300000)
    public void syncToRedis() {
        try {
            List<String> keywords = searchHistoryRepository.findPopularKeywords();

            if (!keywords.isEmpty()) {
                recommendRedisTemplate.delete(POPULAR_KEYWORDS_KEY);
                syncToRedis(keywords);
            }
        } catch (Exception e) {
            System.out.println("redis cache filed : {}" +  e.getMessage());
        }
    }

    @Transactional
    public void create(Long userId, String keyword) {
        try {

            User user = userRepository.findById(userId).orElseThrow(() -> new GeneralException(CommonError._INTERNAL_SERVER_ERROR));

            SearchHistory history = SearchHistory.builder()
                    .user(user)
                    .content(keyword)
                    .createdAt(LocalDateTime.now())
                    .build();
            searchHistoryRepository.save(history);

        recommendRedisTemplate.opsForZSet().incrementScore(POPULAR_KEYWORDS_KEY, keyword, 1);

        } catch (Exception e) {
            System.out.println("create error : " + e.getMessage());
        }
    }

    public List<String> findRecent(Long userId) {
        return searchHistoryRepository.findTop10ByUser_UserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(SearchHistory::getContent)
                .distinct()
                .toList();
    }

    @Transactional
    public void remove(Long userId, String keyword) {
        try {
            searchHistoryRepository.deleteByUserIdAndContent(userId, keyword);

        } catch (Exception e) {
            System.out.println("remove error : " + e.getMessage());
            throw new GeneralException(SearchError._SEARCH_HISTORY_NOT_FOUND);
        }
    }

    @Transactional
    public void removeAll(Long userId) {
        try {
            searchHistoryRepository.deleteByUserId(userId);
        } catch (Exception e) {
            System.out.println("removeAll error : " + e.getMessage());
            throw new GeneralException(SearchError._SEARCH_EXHIBIT_NOT_FOUND);
        }
    }

    public List<String> findPopularKeywords() {
        try {
            Set<String> redisResult = recommendRedisTemplate.opsForZSet()
                    .reverseRange(POPULAR_KEYWORDS_KEY, 0, 4);

            if (redisResult != null && !redisResult.isEmpty()) {
                return redisResult.stream().toList();
            }

            List<String> mysqlResult = searchHistoryRepository.findPopularKeywords();

            if (!mysqlResult.isEmpty()) {
                syncToRedis(mysqlResult);
            }

            return mysqlResult;
        } catch (Exception e) {
            System.out.println("find PopularKeywrods error : " + e.getMessage());
            return searchHistoryRepository.findPopularKeywords();
        }
    }

    private void syncToRedis(List<String> keywords) {
        try {
            for (int i = 0; i < keywords.size(); i++) {
                double score = keywords.size() - i;
                recommendRedisTemplate.opsForZSet().add(POPULAR_KEYWORDS_KEY, keywords.get(i), score);
            }
        } catch (Exception e) {
            System.out.println("syncRedis error : " + e.getMessage());
        }
    }

}
