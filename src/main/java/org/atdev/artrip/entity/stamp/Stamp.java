package org.atdev.artrip.entity.stamp;

import jakarta.persistence.*;
import lombok.*;
import org.atdev.artrip.entity.review.Review;

import java.sql.Timestamp;

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
    @Column(name = "review_id", nullable = false)
    private Review review;

    @Column(name = "acquire_at", nullable = false)
    private Timestamp acquireAt;

    @Column(name = "rank", nullable = false)
    private String rank;

    // 편의 메서드: 스탬프 등급 업데이트
    public void updateRank(String newRank) {
        this.rank = newRank;
    }
}
