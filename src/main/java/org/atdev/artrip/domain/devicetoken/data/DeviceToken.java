package org.atdev.artrip.domain.devicetoken.data;

import jakarta.persistence.*;
import org.atdev.artrip.domain.Enum.Platform;
import org.atdev.artrip.domain.auth.data.User;

import java.time.LocalDateTime;

@Entity
public class DeviceToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_Id")
    private Long tokenId;

    @ManyToOne
    @Column(name = "user_Id")
    private User user;

    @Column(name = "token")
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform")
    private Platform platform;

    @Column(name = "active")
    private boolean active;

    @Column(name = "lastActiveAt")
    private LocalDateTime lastActiveAt;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;
}
