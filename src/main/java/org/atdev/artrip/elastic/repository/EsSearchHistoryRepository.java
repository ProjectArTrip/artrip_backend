package org.atdev.artrip.elastic.repository;

import org.atdev.artrip.elastic.document.EsSearchHistoryDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface EsSearchHistoryRepository extends ElasticsearchRepository<EsSearchHistoryDocument, String> {
    List<EsSearchHistoryDocument> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);
}