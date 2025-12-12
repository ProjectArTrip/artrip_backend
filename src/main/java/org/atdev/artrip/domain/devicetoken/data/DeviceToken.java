package org.atdev.artrip.domain.devicetoken.data;

import jakarta.persistence.*;
import org.atdev.artrip.domain.Enum.Platform;
import org.atdev.artrip.domain.auth.data.User;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "device_token")
public class DeviceToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long tokenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "token", nullable = false, unique = true, length = 255)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform")
    private Platform platform;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "last_active_at")
    private LocalDateTime lastActiveAt;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
