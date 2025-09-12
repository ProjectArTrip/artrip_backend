package org.atdev.artrip.entity.user;

import jakarta.persistence.*;
import lombok.*;
import org.atdev.artrip.entity.Enum.Role;
import org.atdev.artrip.entity.image.Imges;
import org.atdev.artrip.entity.notification.Notification;
import org.atdev.artrip.entity.review.Review;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "user", schema = "art_dev")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Enumerated(EnumType.STRING) // 여기서 STRING으로 매핑
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "stamp_num")
    private Byte stampNum;

    @Column(name = "push_token")
    private String pushToken;

}
