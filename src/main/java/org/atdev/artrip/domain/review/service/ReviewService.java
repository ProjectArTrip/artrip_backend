package org.atdev.artrip.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;




}
