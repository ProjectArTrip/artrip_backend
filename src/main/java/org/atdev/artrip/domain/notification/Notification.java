package org.atdev.artrip.domain.notification;

import jakarta.persistence.*;
import lombok.*;
import org.atdev.artrip.domain.auth.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_read")
    private Boolean isRead;  // Byte → Boolean로 변경, true = 읽음, false = 안 읽음

    @Column(name = "extra")
    private String extra;

    // 편의 메서드: 읽음/안 읽음 상태 확인
    public boolean isRead() {
        return Boolean.TRUE.equals(isRead);
    }

    public void markAsRead() {
        this.isRead = true;
    }

    public void markAsUnread() {
        this.isRead = false;
    }
}
