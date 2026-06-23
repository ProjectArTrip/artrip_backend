package org.atdev.artrip.domain.notice;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.atdev.artrip.constants.NotificationAction;
import org.atdev.artrip.domain.auth.User;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "user_notice")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserNotice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_notice_id")
    private Long userNoticeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationAction action;

    private Long referenceId;
    private String title;
    private String body;

    @Column(nullable = false)
    private boolean isRead;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime readAt;

    private UserNotice(User user, NotificationAction action, Long referenceId, String title, String body) {
        this.user = user;
        this.action = action;
        this.referenceId = referenceId;
        this.title = title;
        this.body = body;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }

    public static UserNotice create(User user, NotificationAction action, Long referenceId, String title, String body) {
        return new UserNotice(
                user,
                action,
                referenceId,
                title,
                body
        );
    }

    public void markAsRead() {
        if (!this.isRead) {
            this.isRead = true;
            this.readAt = LocalDateTime.now();
        }
    }
}
