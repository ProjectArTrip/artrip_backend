package org.atdev.artrip.search.repository;

import org.atdev.artrip.entity.user.SearchHistory;
import org.atdev.artrip.search.document.SearchHistoryDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface SearchHistoryRepository extends ElasticsearchRepository<SearchHistoryDocument, String> {
    List<SearchHistoryDocument> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);
}