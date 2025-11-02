package org.atdev.artrip.domain.review.data;

import jakarta.persistence.*;
import lombok.*;
import org.atdev.artrip.domain.Imges;
import org.atdev.artrip.domain.Stamp;
import org.atdev.artrip.domain.auth.data.User;
import org.atdev.artrip.domain.exhibit.data.Exhibit;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "review", schema = "art_dev")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exhibit_id", nullable = false)
    private Exhibit exhibit;

    @Column(name = "content", nullable = false, length = 2000)
    private String content;

    @Column(name = "visit_date")
    private Timestamp visitDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Stamp> stamps;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Imges> images;

    // 편의 메서드: 리뷰 업데이트
    public void updateContent(String newContent, LocalDateTime updatedAt) {
        this.content = newContent;
        this.updatedAt = updatedAt;
    }
}
