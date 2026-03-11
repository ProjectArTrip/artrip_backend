package org.atdev.artrip.domain.notice;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.atdev.artrip.constants.Role;
import org.atdev.artrip.domain.auth.User;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "notice")
@NoArgsConstructor
@AllArgsConstructor
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long noticeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime registerDate;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime updateDate;

    private String title;
    private String content;

    public static Notice create(User user, String title, String contetn) {
        LocalDateTime now =  LocalDateTime.now();

        return new Notice(
                null,
                user,
                user.getRole(),
                now,
                now,
                title,
                contetn
        );
    }

    public static Notice of (Long noticeId, User user, LocalDateTime registerDate, LocalDateTime updateDate, String title, String content) {
        return new Notice(
                noticeId,
                user,
                user.getRole(),
                registerDate,
                updateDate,
                title,
                content
        );
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
        this.updateDate = LocalDateTime.now();
    }

}
