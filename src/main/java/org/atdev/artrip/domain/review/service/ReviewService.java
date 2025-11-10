package org.atdev.artrip.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.review.data.ReviewImage;
import org.atdev.artrip.domain.auth.data.User;
import org.atdev.artrip.domain.auth.repository.UserRepository;
import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.atdev.artrip.domain.exhibit.repository.ExhibitRepository;
import org.atdev.artrip.domain.review.converter.ReviewConverter;
import org.atdev.artrip.domain.review.data.Review;
import org.atdev.artrip.domain.review.repository.ReviewImageRepository;
import org.atdev.artrip.domain.review.repository.ReviewRepository;
import org.atdev.artrip.domain.review.web.dto.ReviewCreateRequest;
import org.atdev.artrip.domain.review.web.dto.ReviewResponse;
import org.atdev.artrip.global.apipayload.code.status.ErrorStatus;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.global.s3.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final UserRepository userRepository;
    private final ExhibitRepository exhibitRepository;
    private final ReviewConverter reviewConverter;
    private final S3Service s3Service;


    @Transactional
    public ReviewResponse createReview(Long exhibitId, ReviewCreateRequest request, List<MultipartFile> images, Long userId){


        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));

        Exhibit exhibit = exhibitRepository.findById(exhibitId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._EXHIBIT_NOT_FOUND));

        Review review = reviewConverter.toEntity(user,exhibit,request);
        reviewRepository.save(review);

        List<String> s3Urls = (images == null || images.isEmpty())
                ? new ArrayList<>()
                : s3Service.upload(images);

        List<ReviewImage> reviewImages = reviewConverter.toReviewImage(review,s3Urls);

        if (reviewImages!=null&&!reviewImages.isEmpty()){
            reviewImageRepository.saveAll(reviewImages);
            review.setImages(reviewImages);
        }

        return reviewConverter.toReviewResponse(review,exhibit);
    }


}
