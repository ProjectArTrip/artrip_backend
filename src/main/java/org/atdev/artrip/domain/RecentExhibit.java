package org.atdev.artrip.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.atdev.artrip.domain.exhibit.data.Exhibit;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "recent_exhibit", schema = "art_dev")
@Data
public class RecentExhibit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recent_exhibit_id")
    private long recentExhibitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exhibit_id")
    private Exhibit exhibit;

    @Column(name = "view_at")
    private LocalDateTime viewAt;
}
