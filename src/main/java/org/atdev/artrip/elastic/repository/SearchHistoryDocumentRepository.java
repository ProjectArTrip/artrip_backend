package org.atdev.artrip.elastic.repository;

import org.atdev.artrip.elastic.document.SearchHistoryDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface SearchHistoryDocumentRepository extends ElasticsearchRepository<SearchHistoryDocument, Long> {

    List<SearchHistoryDocument> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);

    void deleteByUserIdAndContent(Long userId, String content);
    void deleteByUserId(Long userId);
}