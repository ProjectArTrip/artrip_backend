package org.atdev.artrip.domain.auth;

import jakarta.persistence.*;
import lombok.*;
import org.atdev.artrip.constants.Provider;
import org.atdev.artrip.controller.dto.response.SocialUserInfo;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "social_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialAccounts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "social_id")
    private Long socialId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private Provider provider;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "refresh_token")
    private String refreshToken;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static SocialAccounts create(User user, SocialUserInfo info) {
        return SocialAccounts.builder()
                .user(user)
                .provider(info.getProvider())
                .providerId(info.getProviderId())
                .build();
    }

    public static SocialAccounts of(User user, SocialUserInfo info) {
        SocialAccounts social = new SocialAccounts();
        social.user = user;
        social.provider = info.getProvider();
        social.providerId = info.getProviderId();
        return social;
    }
}
