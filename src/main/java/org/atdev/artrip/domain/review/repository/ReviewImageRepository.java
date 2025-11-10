package org.atdev.artrip.domain.review.repository;

import org.atdev.artrip.domain.review.data.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage,Long> {

}
