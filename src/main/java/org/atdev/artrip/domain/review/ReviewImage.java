package org.atdev.artrip.domain.review;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "img_id")
    private long imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", referencedColumnName = "review_id")
    private Review review;

    @Column(name = "displayOrder", nullable = false)
    private int displayOrder;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "curation_id")
    private Integer curationId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
