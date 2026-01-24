package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.search.SearchHistory;
import org.atdev.artrip.global.apipayload.code.status.SearchErrorCode;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.repository.SearchHistoryRepository;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.service.dto.command.SearchHistoryCommand;
import org.atdev.artrip.service.dto.result.SearchHistoryResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;
    private final UserRepository userRepository;

    private static final int MAX_CONTENT_LENGTH = 10;

    @Transactional
    public void saveSearchHistory(Long userId, String content) {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new GeneralException(UserErrorCode._USER_NOT_FOUND));

        if (content == null || content.isBlank()) {
            return;
        }

        String trimmedContent = content.trim();
        if (trimmedContent.length() > MAX_CONTENT_LENGTH) {
            throw new GeneralException(SearchErrorCode._SEARCH_HISTORY_CONTENT_TOO_LONG);
        }

        try {
            searchHistoryRepository.deleteDuplicate(userId, content);

            SearchHistory searchHistory = SearchHistory.of(null, user, content, LocalDate.now());
            searchHistoryRepository.save(searchHistory);
        } catch (Exception e) {
            throw new GeneralException(SearchErrorCode._SEARCH_HISTORY_SAVE_FAILED);
        }
    }

    @Transactional(readOnly = true)
    public List<SearchHistoryResult> getRecentSearchHistory(SearchHistoryCommand command) {
        if (!userRepository.existsById(command.userId())) {
            throw new GeneralException(UserErrorCode._USER_NOT_FOUND);
        }

        List<SearchHistory> histories = searchHistoryRepository.findRecent(command.userId());
        return histories.stream()
                .map(SearchHistoryResult::from)
                .toList();
    }

    @Transactional
    public void deleteSearchHistory(SearchHistoryCommand command) {
        SearchHistory history = searchHistoryRepository.findById(command.searchHistoryId())
                .orElseThrow(() -> new GeneralException(SearchErrorCode._SEARCH_HISTORY_NOT_FOUND));


        if (!history.getUser().getUserId().equals(command.userId())) {
            throw new GeneralException(SearchErrorCode._SEARCH_HISTORY_DELETE_FORBIDDEN);
        }

        searchHistoryRepository.delete(history);
    }
}
