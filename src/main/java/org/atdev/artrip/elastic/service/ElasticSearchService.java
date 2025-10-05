package org.atdev.artrip.elastic.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.domain.SearchHistory;
import org.atdev.artrip.domain.User;
import org.atdev.artrip.global.apipayload.code.status.ErrorStatus;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.elastic.document.EsSearchHistoryDocument;
import org.atdev.artrip.elastic.repository.EsJpaSearchHistoryRepository;
import org.atdev.artrip.elastic.repository.EsSearchHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElasticSearchService {

    private final EsSearchHistoryRepository esSearchHistoryRepository;
    private final EsJpaSearchHistoryRepository esJpaSearchHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void saveKeyword(Long userId, String keyword){
        SearchHistory history = new SearchHistory();

        User user = userRepository.findById(userId).orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST));

        history.setUser(user);
        history.setContent(keyword);
        esJpaSearchHistoryRepository.save(history);

        EsSearchHistoryDocument doc = new EsSearchHistoryDocument();
        doc.setUserId(userId);
        doc.setContent(keyword);
        doc.setCreatedAt(System.currentTimeMillis());
        esSearchHistoryRepository.save(doc);
    }

    public List<String> getRecentKeywords(Long userId){
        return esSearchHistoryRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(EsSearchHistoryDocument::getContent)
                .toList();
    }
}
