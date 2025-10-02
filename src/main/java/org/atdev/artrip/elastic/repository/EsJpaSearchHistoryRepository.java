package org.atdev.artrip.elastic.repository;

import org.atdev.artrip.domain.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EsJpaSearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    List<SearchHistory> findTop10ByUser_UserIdOrderByCreatedAtDesc(Long userId);
}
