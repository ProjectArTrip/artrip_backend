package org.atdev.artrip.domain.search.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.domain.auth.data.User;
import org.atdev.artrip.domain.auth.repository.UserRepository;
import org.atdev.artrip.domain.search.data.SearchHistory;
import org.atdev.artrip.domain.search.repository.SearchHistoryRepository;
import org.atdev.artrip.elastic.document.SearchHistoryDocument;
import org.atdev.artrip.elastic.repository.SearchHistoryDocumentRepository;
import org.atdev.artrip.global.apipayload.code.status.ErrorStatus;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchHistoryService {

    private final SearchHistoryDocumentRepository searchHistoryDocumentRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final UserRepository userRepository;


    @Transactional
    public void create(Long userId, String keyword) {
        //TODO: 배포 전 로그 레벨 조정
        try {
            log.debug("Saving search history for userId: {}, keyword: {}", userId, keyword);

            User user = userRepository.findById(userId).orElseThrow(() -> new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR));

            SearchHistory history = SearchHistory.builder()
                    .user(user)
                    .content(keyword)
                    .createdAt(LocalDateTime.now())
                    .build();
            searchHistoryRepository.save(history);

            SearchHistoryDocument doc = SearchHistoryDocument.builder()
                    .userId(userId)
                    .content(keyword)
                    .createdAt(LocalDateTime.now())
                    .build();
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

            searchHistoryDocumentRepository.deleteByUserIdAndContent(userId, keyword);

            log.debug("Deleted search history for userId: {}, keyword: {}", userId, keyword);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void removeAll(Long userId) {
        try {
            log.debug("Deleting all search history for userId: {}", userId);

            searchHistoryRepository.deleteByUserId(userId);

            searchHistoryDocumentRepository.deleteByUserId(userId);

            log.debug("Deleted all search history for userId: {}", userId);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
    }
}
