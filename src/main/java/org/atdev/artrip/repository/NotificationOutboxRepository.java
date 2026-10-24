package org.atdev.artrip.repository;

import org.atdev.artrip.infra.notification.outbox.NotificationOutbox;
import org.atdev.artrip.infra.notification.outbox.OutboxStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationOutboxRepository extends JpaRepository<NotificationOutbox, Long> {

    @Query("""
            select o.outboxId from NotificationOutbox o
            where (o.status = :pending and o.nextRetryAt <= :now)
               or (o.status = :processing and o.lockedUntil < :now)
            order by o.outboxId
            """)
    List<Long> findProcessableIds(@Param("pending") OutboxStatus pending,
                                  @Param("processing") OutboxStatus processing,
                                  @Param("now") LocalDateTime now,
                                  Pageable pageable);

    default List<Long> findProcessableIds(LocalDateTime now, int limit) {
        return findProcessableIds(OutboxStatus.PENDING, OutboxStatus.PROCESSING, now, PageRequest.of(0, limit));
    }

    /**
     * 처리 가능한 상태일 때만 PROCESSING으로 선점한다. 즉시 발송과 폴러가 동시에 집어도 한쪽만 1을 받는다.
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update NotificationOutbox o
               set o.status = :processing, o.lockedUntil = :lockedUntil
             where o.outboxId = :outboxId
               and ((o.status = :pending and o.nextRetryAt <= :now)
                 or (o.status = :processing and o.lockedUntil < :now))
            """)
    int claim(@Param("outboxId") Long outboxId,
              @Param("pending") OutboxStatus pending,
              @Param("processing") OutboxStatus processing,
              @Param("now") LocalDateTime now,
              @Param("lockedUntil") LocalDateTime lockedUntil);

    default boolean claim(Long outboxId, LocalDateTime now, LocalDateTime lockedUntil) {
        return claim(outboxId, OutboxStatus.PENDING, OutboxStatus.PROCESSING, now, lockedUntil) == 1;
    }

    @Modifying
    @Query("delete from NotificationOutbox o where o.status = :status and o.processedAt < :before")
    int deleteProcessedBefore(@Param("status") OutboxStatus status, @Param("before") LocalDateTime before);
}
