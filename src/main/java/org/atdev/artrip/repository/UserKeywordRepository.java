package org.atdev.artrip.repository;

import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.keyword.UserKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserKeywordRepository extends JpaRepository<UserKeyword, Long> {
    void deleteByUser(User user);

    List<UserKeyword> findAllByUserUserId(Long userId);

    List<UserKeyword> findByUser_UserId(Long userId);
}