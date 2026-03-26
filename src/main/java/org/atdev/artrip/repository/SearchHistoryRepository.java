package org.atdev.artrip.repository;

import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.search.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    @Query(value = """
            SELECT * FROM search_history 
            WHERE user_id = :userId 
            ORDER BY created_at DESC, search_history_id DESC
            LIMIT 10
            """, nativeQuery = true)
    List<SearchHistory> findRecent(@Param("userId") Long userId);

    @Modifying
    @Query(value = """
            DELETE FROM search_history 
            WHERE user_id = :userId AND content = :content
            """, nativeQuery = true)
    void deleteDuplicate(@Param("userId") Long userId, @Param("content") String content);

    long countByUser_UserId(Long userId);

    @Query(value = """
            select * 
            from search_history
            where user_id = :userId
            order by created_at asc, search_history_id asc
            limit 1
            """, nativeQuery = true)
    Optional<SearchHistory> findOldestSearchHistory(@Param("userId") Long userId);

    void deleteAllByUser(User user);
}
