package org.atdev.artrip.repository;

import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.domain.review.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage,Long> {

    @Modifying
    @Query("DELETE FROM ReviewImage ri WHERE ri.review.user.userId = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    void deleteByReviewExhibit(Exhibit exhibit);
}
