package org.atdev.artrip.domain.keyword.repository;

import org.atdev.artrip.domain.Enum.KeywordType;
import org.atdev.artrip.domain.auth.data.User;
import org.atdev.artrip.domain.keyword.data.Keyword;
import org.atdev.artrip.domain.keyword.data.UserKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserKeywordRepository extends JpaRepository<UserKeyword, Long> {
    void deleteByUser(User user);

    List<UserKeyword> findAllByUserUserId(Long userId);

    List<UserKeyword> findByUser_UserId(Long userId);
}