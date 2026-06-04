package org.atdev.artrip.repository;

import org.atdev.artrip.domain.notice.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update notice set user_id = null where user_id = :userId", nativeQuery = true)
    void detachUser(@Param("userId") Long userId);
}
