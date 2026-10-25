package org.atdev.artrip.infra.notification.outbox;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "notification_outbox",
        indexes = @Index(name = "idx_outbox_poll", columnList = "status, next_retry_at"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationOutbox {

    private static final int MAX_ERROR_LENGTH = 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "outbox_id")
    private Long outboxId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private OutboxEventType eventType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", nullable = false, columnDefinition = "json")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OutboxStatus status;

    @Column(name = "inbox_saved", nullable = false)
    private boolean inboxSaved;

    @Column(name = "push_cursor")
    private Long pushCursor;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "next_retry_at", nullable = false)
    private LocalDateTime nextRetryAt;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Column(name = "last_error", length = MAX_ERROR_LENGTH)
    private String lastError;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    private NotificationOutbox(OutboxEventType eventType, String payload, LocalDateTime now) {
        this.eventType = eventType;
        this.payload = payload;
        this.status = OutboxStatus.PENDING;
        this.inboxSaved = false;
        this.retryCount = 0;
        this.nextRetryAt = now;
        this.createdAt = now;
    }

    public static NotificationOutbox pending(OutboxEventType eventType, String payload, LocalDateTime now) {
        return new NotificationOutbox(eventType, payload, now);
    }

    public void markInboxSaved() {
        this.inboxSaved = true;
    }

    public void advancePushCursor(Long lastUserId, LocalDateTime lockedUntil) {
        this.pushCursor = lastUserId;
        this.lockedUntil = lockedUntil;
    }

    public void markSent(LocalDateTime now) {
        this.status = OutboxStatus.SENT;
        this.processedAt = now;
        this.lockedUntil = null;
        this.lastError = null;
    }

    public void scheduleRetry(String error, LocalDateTime nextRetryAt) {
        this.status = OutboxStatus.PENDING;
        this.retryCount++;
        this.nextRetryAt = nextRetryAt;
        this.lockedUntil = null;
        this.lastError = truncate(error);
    }

    public void markFailed(String error, LocalDateTime now) {
        this.status = OutboxStatus.FAILED;
        this.processedAt = now;
        this.lockedUntil = null;
        this.lastError = truncate(error);
    }

    private static String truncate(String error) {
        if (error == null || error.length() <= MAX_ERROR_LENGTH) return error;
        return error.substring(0, MAX_ERROR_LENGTH);
    }
}
