package org.atdev.artrip.search.repository;

import org.atdev.artrip.domain.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaSearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    List<SearchHistory> findTop10ByUser_UserIdOrderByCreatedAtDesc(Long userId);
}
