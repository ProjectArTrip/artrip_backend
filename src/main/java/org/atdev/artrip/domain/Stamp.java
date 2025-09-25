package org.atdev.artrip.domain;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "stamp", schema = "art_dev")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stamp_id")
    private Long stampId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Column(name = "acquire_at", nullable = false)
    private LocalDateTime acquireAt;

    @Column(name = "user_rank", nullable = false)
    private String rank;

    public void updateRank(String newRank) {
        this.rank = newRank;
    }
}
