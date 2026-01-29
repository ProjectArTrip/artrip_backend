package org.atdev.artrip.domain.stamp;

import jakarta.persistence.*;
import lombok.*;
import org.atdev.artrip.domain.review.Review;

import java.time.LocalDateTime;

@Entity
@Table(name = "stamp")
@Getter
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
