package org.atdev.artrip.repository;

import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.keyword.UserKeyword;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserKeywordRepository extends JpaRepository<UserKeyword, Long> {
    @Query("select uk from UserKeyword uk join fetch uk.keyword k where uk.user.userId = :userId")
    List<UserKeyword> findAllByUserIdWithKeyword(@Param("userId") Long userId);

    List<UserKeyword> findByUser_UserId(Long userId);

    @Modifying
    @Query("delete from UserKeyword uk where uk.user.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Query(value = """
            select *
            from user_keyword uk
            where uk.user_id = :userId
            order by rand()
            """, nativeQuery = true)
    List<UserKeyword> findRandomKeywordByUserId(@Param("userId") Long userId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM UserKeyword uk WHERE uk.user = :user")
    void deleteAllByUser(@Param("user") User user);

    @Query(value = """
            select distinct uk.user.userId
            from UserKeyword uk
            where uk.keyword.keywordId in :keywordIds
            """)
    Set<Long> findUserIdsWithKeywordIn(@Param("keywordIds") Set<Long> keywordIds);
}