package org.atdev.artrip.domain.auth;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.atdev.artrip.constants.Role;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "name", nullable = false)
    private String name;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING) // 여기서 STRING으로 매핑
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "stamp_num")
    private Byte stampNum;

    @Column(name = "profile_image_Url")
    private String profileImageUrl;

    @Column(name = "nick_name")
    private String nickName;

    @Builder.Default
    @Column(nullable = false)
    private boolean onboardingCompleted=false;

    @Email
    @Column(name = "email",nullable = true)
    private String email;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SocialAccounts> socialAccounts = new ArrayList<>();

    public boolean updateUserInfo(String name, String email) {
        boolean changed = false;

        if (name != null && !name.equals(this.name)) {
            this.name = name;
            changed = true;
        }

        if (email != null && !email.equals(this.email)) {
            this.email = email;
            changed = true;
        }

        return changed;
    }

    public void updateNickname(String nickName) {
        this.nickName=nickName.trim();
    }

    public void updateProfileImage(String url){
        this.profileImageUrl=url;
    }
}
