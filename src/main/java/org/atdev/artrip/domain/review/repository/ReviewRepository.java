package org.atdev.artrip.domain.review.repository;

import org.atdev.artrip.domain.auth.data.User;
import org.atdev.artrip.domain.review.data.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {

    @Query("select r from Review r where r.user.userId = :userId order by r.reviewId desc")
    Slice<Review> findTopByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("select r from Review r where r.user.userId = :userId and r.reviewId < :cursor order by r.reviewId desc")
    Slice<Review> findByUserIdAndIdLessThan(@Param("userId") Long userId,
                                                        @Param("cursor") Long cursor,
                                                        Pageable pageable);

    @Query("select r from Review r where r.exhibit.exhibitId = :exhibitId order by r.reviewId desc")
    Slice<Review> findTopByExhibitId(@Param("exhibitId") Long exhibitId, Pageable pageable);

    @Query("select r from Review r where r.exhibit.exhibitId = :exhibitId and r.reviewId < :cursor order by r.reviewId desc")
    Slice<Review> findByExhibitIdAndIdLessThan(@Param("exhibitId") Long exhibitId,
                                            @Param("cursor") Long cursor,
                                            Pageable pageable);

    long countByExhibit_ExhibitId(Long exhibitId);
}
