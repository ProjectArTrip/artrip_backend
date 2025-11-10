package org.atdev.artrip.domain.review.repository;

import org.atdev.artrip.domain.auth.data.User;
import org.atdev.artrip.domain.review.data.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {


}
