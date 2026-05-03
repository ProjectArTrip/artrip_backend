package org.atdev.artrip.repository;

import org.atdev.artrip.constants.NotificationAction;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.notice.UserNotice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserNoticeRepository extends JpaRepository<UserNotice, Long> {

    @Query("""
            select un
            from UserNotice un
            where un.user = :user
            and (:cursor is null or un.userNoticeId < :cursor)
            and (:action is null or un.action = :action)
            order by un.createdAt desc
            """)
    Slice<UserNotice> findAllByUser(@Param("user") User user, @Param("cursor") Long cursor, @Param("action") NotificationAction action, Pageable pageable);

    @Query("""
            select case when count(un) > 0 then true else false end
            from UserNotice un
            where un.user = :user
            and un.isRead = false
            """)
    boolean existsUnreadByUser(@Param("user") User user);

    @Modifying
    @Query("""
            update UserNotice n
            set n.isRead = true, n.readAt = CURRENT_TIMESTAMP
            where n.user = :user and n.isRead = false
            """)
    int markAllAsRead(@Param("user") User user);

}
