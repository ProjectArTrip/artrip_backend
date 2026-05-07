package org.atdev.artrip.domain.review;

import jakarta.persistence.*;
import lombok.*;
import org.atdev.artrip.constants.ReviewStatus;
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

    @Enumerated(EnumType.STRING)
    @Column
    private ReviewStatus status;

    @Column(length = 500)
    private String rejectionReason;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Stamp> stamps;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<ReviewImage> images= new ArrayList<>();

    public void updateData(String content, LocalDate visitDate, LocalDateTime updatedAt) {
        if (content != null) this.content = content;
        if (visitDate != null) this.visitDate = visitDate;
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

    public static Review create(User user, Exhibit exhibit, String content, LocalDate date, List<String> s3Urls) {
        Review review = Review.builder()
                .user(user)
                .exhibit(exhibit)
                .content(content)
                .visitDate(date)
                .createdAt(LocalDateTime.now())
                .status(ReviewStatus.PENDING)
                .build();

        if (s3Urls != null && !s3Urls.isEmpty()) {
            review.addImages(s3Urls);
        }

        return review;
    }

    public void reject(String reason) {
        this.status = ReviewStatus.REJECTED;
        this.rejectionReason = reason;
        this.rejectedAt = LocalDateTime.now();
    }

    public void approve() {
        this.status = ReviewStatus.APPROVED;
        this.updatedAt = LocalDateTime.now();
    }


    public void resubmit() {
        this.status = ReviewStatus.PENDING;
        this.rejectionReason = null;
        this.rejectedAt = null;
        this.updatedAt = LocalDateTime.now();
    }
}
