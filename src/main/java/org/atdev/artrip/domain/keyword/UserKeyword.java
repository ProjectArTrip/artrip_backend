package org.atdev.artrip.domain.keyword;

import jakarta.persistence.*;
import lombok.*;
import org.atdev.artrip.domain.auth.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_keyword")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long Id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id")
    private Keyword keyword;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    public static UserKeyword create(User user, Keyword keyword) {
        return UserKeyword.builder()
                .user(user)
                .keyword(keyword)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
