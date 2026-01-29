package org.atdev.artrip.domain.review;

import jakarta.persistence.*;
import lombok.*;
import org.atdev.artrip.domain.stamp.Stamp;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.exhibit.Exhibit;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "review")
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

    @Column(name = "content", nullable = false, length = 500)
    private String content;

    @Column(name = "visit_date")
    private LocalDate visitDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Stamp> stamps;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<ReviewImage> images= new ArrayList<>();

    public void updateContent(String newContent, LocalDateTime updatedAt) {
        this.content = newContent;
        this.updatedAt = updatedAt;
    }

    public void addImages(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) return;

        int currentOrder = this.images.size() + 1;
        for (String url : imageUrls) {
            this.images.add(ReviewImage.builder()
                    .review(this)
                    .imageUrl(url)
                    .displayOrder(currentOrder++)
                    .build());
        }
    }
}
