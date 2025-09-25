package org.atdev.artrip.search.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.entity.user.SearchHistory;
import org.atdev.artrip.search.document.SearchHistoryDocument;
import org.atdev.artrip.search.repository.JpaSearchHistoryRepository;
import org.atdev.artrip.search.repository.SearchHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ElasticSearchService {

    private final SearchHistoryRepository searchHistoryRepository;
    private final JpaSearchHistoryRepository jpaSearchHistoryRepository;

    public void saveKeyword(Long userId, String keyword){
        SearchHistory history = new SearchHistory();
        // TODO: User 객체를 조회하여 설정 예정
//        history.setUser(user);
        history.setContent(keyword);
        jpaSearchHistoryRepository.save(history);

        SearchHistoryDocument doc = new SearchHistoryDocument();
        doc.setUserId(userId);
        doc.setContent(keyword);
        doc.setCreatedAt(System.currentTimeMillis());
        searchHistoryRepository.save(doc);
    }

    public List<String> getRecentKeywords(Long userId){
        return searchHistoryRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(SearchHistoryDocument::getContent)
                .toList();
    }
}
