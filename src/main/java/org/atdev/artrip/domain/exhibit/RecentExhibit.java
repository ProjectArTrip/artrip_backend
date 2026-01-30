package org.atdev.artrip.domain.exhibit;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.atdev.artrip.domain.auth.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "recent_exhibit", indexes = {
        @Index(name = "idx_user_view_at", columnList = "user_id, view_at DESC")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecentExhibit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recent_exhibit_id")
    private Long recentExhibitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exhibit_id")
    private Exhibit exhibit;

    @Column(name = "view_at")
    private LocalDateTime viewAt;

    public RecentExhibit(User user, Exhibit exhibit) {
        this.user = user;
        this.exhibit = exhibit;
        this.viewAt = LocalDateTime.now();
    }

    public void updateViewAt() {
        this.viewAt = LocalDateTime.now();
    }
}
