package org.atdev.artrip.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.auth.data.User;
import org.atdev.artrip.domain.auth.repository.UserRepository;
import org.atdev.artrip.domain.review.data.Review;
import org.atdev.artrip.domain.review.repository.ReviewRepository;
import org.atdev.artrip.domain.review.response.ReviewResponse;
import org.atdev.artrip.global.s3.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;


    public ReviewResponse createReview(LocalDate date , String content, List<MultipartFile> images, Long userId){


        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));

        List<String> s3Urls = (images == null || images.isEmpty())
                ? new ArrayList<>()
                : s3Service.upload(images);

        reviewRepository.save();



        return null;
    }


}
