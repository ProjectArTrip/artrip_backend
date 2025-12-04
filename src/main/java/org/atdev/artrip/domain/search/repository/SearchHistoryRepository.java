package org.atdev.artrip.domain.search.repository;

import org.atdev.artrip.domain.search.data.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    List<SearchHistory> findTop10ByUser_UserIdOrderByCreatedAtDesc(Long userId);

    @Modifying
    @Query("DELETE FROM SearchHistory sh WHERE sh.user.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM SearchHistory sh WHERE sh.user.userId = :userId AND sh.content = :content")
    void deleteByUserIdAndContent(@Param("userId") Long userId, @Param("content") String content);

    @Query(
            "SELECT sh.content " +
                    "FROM SearchHistory sh " +
                    "GROUP BY sh.content ORDER BY COUNT(sh) DESC LIMIT 5"
    )
    List<String> findPopularKeywords();
}
