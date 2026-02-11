package org.atdev.artrip.repository;

import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.keyword.Keyword;
import org.atdev.artrip.domain.keyword.UserKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserKeywordRepository extends JpaRepository<UserKeyword, Long> {
    @Query("select uk from UserKeyword uk join fetch uk.keyword k where uk.user.userId = :userId")
    List<UserKeyword> findAllByUserIdWithKeyword(@Param("userId") Long userId);

    List<UserKeyword> findByUser_UserId(Long userId);

    @Modifying
    @Query("delete from UserKeyword uk where uk.user.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}