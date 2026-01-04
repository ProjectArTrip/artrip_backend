package org.atdev.artrip.repository;

import org.atdev.artrip.domain.review.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage,Long> {

    List<ReviewImage> findByReview_ReviewId(Long reviewId);


}
