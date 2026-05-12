package org.atdev.artrip.domain.maintenance;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.atdev.artrip.constants.MaintenanceState;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Entity
@Table(name = "maintenance")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maintenance_id")
    private Long maintenanceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MaintenanceState state;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    @Column(nullable = false, length = 50)
    private String buttonText;

    @Column(nullable = false)
    private boolean forceExit;

    @Column(nullable = false)
    private int refreshAfterSeconds;

    @Column(nullable = false)
    private long version;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private Maintenance(
            Long maintenanceId,
            MaintenanceState state,
            String title,
            String message,
            LocalDateTime startAt,
            LocalDateTime endAt,
            String buttonText,
            boolean forceExit,
            int refreshAfterSeconds,
            long version
    ) {
        this.maintenanceId = maintenanceId;
        this.state = state;
        this.title = title;
        this.message = message;
        this.startAt = startAt;
        this.endAt = endAt;
        this.buttonText = buttonText;
        this.forceExit = forceExit;
        this.refreshAfterSeconds = refreshAfterSeconds;
        this.version = version;
    }

    public static Maintenance create(
            MaintenanceState state,
            String title,
            String message,
            LocalDateTime startAt,
            LocalDateTime endAt,
            String buttonText,
            boolean forceExit,
            int refreshAfterSeconds
    ) {
        return new Maintenance(
                null,
                state,
                title,
                message,
                startAt,
                endAt,
                buttonText,
                forceExit,
                refreshAfterSeconds,
                1L
        );
    }

    public static Maintenance 정createOrUpdate(
            Optional<Maintenance> existing,
            MaintenanceState state,
            String title,
            String message,
            LocalDateTime startAt,
            LocalDateTime endAt,
            String buttonText,
            boolean forceExit,
            int refreshAfterSeconds
    ) {
        if (existing.isPresent()) {
            existing.get().update(state, title, message, startAt, endAt, buttonText, forceExit, refreshAfterSeconds);
            return existing.get();
        }
        return Maintenance.create(state, title, message, startAt, endAt, buttonText, forceExit, refreshAfterSeconds);
    }

    public void update(
            MaintenanceState state,
            String title,
            String message,
            LocalDateTime startAt,
            LocalDateTime endAt,
            String buttonText,
            boolean forceExit,
            int refreshAfterSeconds
    ) {
        this.state = state;
        this.title = title;
        this.message = message;
        this.startAt = startAt;
        this.endAt = endAt;
        this.buttonText = buttonText;
        this.forceExit = forceExit;
        this.refreshAfterSeconds = refreshAfterSeconds;
        this.version = this.version + 1L;
    }

    public boolean isActiveAt(LocalDateTime now) {
        if (state == MaintenanceState.NORMAL) {
            return false;
        }
        return !now.isBefore(startAt) && !now.isAfter(endAt);
    }
}
