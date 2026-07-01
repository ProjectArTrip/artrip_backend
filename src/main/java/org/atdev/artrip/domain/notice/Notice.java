package org.atdev.artrip.domain.notice;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.atdev.artrip.constants.Role;
import org.atdev.artrip.domain.auth.User;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "notice")
@NoArgsConstructor
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long noticeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private String title;
    private String content;

    public static Notice create(User user, String title, String content) {
        LocalDateTime now = LocalDateTime.now();

        Notice notice = new Notice();
        notice.user = user;
        notice.title = title;
        notice.content = content;
        notice.createdAt = now;
        notice.updatedAt = now;
        notice.role = user.getRole();
        return notice;
    }

    public static Notice of(Long noticeId, User user, LocalDateTime createdAt, LocalDateTime updatedAt, String title, String content) {
        Notice notice = new Notice();
        notice.noticeId = noticeId;
        notice.user = user;
        notice.role = user.getRole();
        notice.createdAt = createdAt;
        notice.updatedAt = updatedAt;
        notice.title = title;
        notice.content = content;

        return notice;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

}
