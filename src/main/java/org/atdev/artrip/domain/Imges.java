package org.atdev.artrip.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.atdev.artrip.domain.auth.data.User;
import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.atdev.artrip.domain.review.data.Review;

import java.time.LocalDateTime;

@Entity
@Table(name = "imges", schema = "art_dev")
@Data
public class Imges {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "img_id")
    private long imgId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", referencedColumnName = "review_id")
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY) // 이미지 → 전시 (N:1)
    @JoinColumn(name = "exhibit_id", referencedColumnName = "exhibit_id")
    private Exhibit exhibit;

    @ManyToOne(fetch = FetchType.LAZY) // 이미지 → 유저 (N:1)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "curation_id")
    private Integer curationId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
