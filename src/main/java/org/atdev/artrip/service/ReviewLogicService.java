package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.domain.review.Review;
import org.atdev.artrip.domain.review.ReviewImage;
import org.atdev.artrip.global.apipayload.code.status.ExhibitErrorCode;
import org.atdev.artrip.global.apipayload.code.status.ReviewErrorCode;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.repository.ExhibitRepository;
import org.atdev.artrip.repository.ReviewRepository;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.service.dto.command.ReviewCreateCommand;
import org.atdev.artrip.service.dto.command.ReviewUpdateCommand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewLogicService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ExhibitRepository exhibitRepository;

    private User findUserById(Long userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException(UserErrorCode._USER_NOT_FOUND));
    }

    private Exhibit findExhibitById(Long exhibitId) {
        return exhibitRepository.findById(exhibitId)
                .orElseThrow(() -> new GeneralException(ExhibitErrorCode._EXHIBIT_NOT_FOUND));
    }

    @Transactional
    public void saveReviewWithImages(ReviewCreateCommand command, List<String> s3Urls) {

        User user = findUserById(command.userId());
        Exhibit exhibit = findExhibitById(command.exhibitId());

        Review review = command.toEntity(user, exhibit);

        if (s3Urls != null && !s3Urls.isEmpty()) {
            review.addImages(s3Urls);
        }

        reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public List<String> getUrlsToDelete(Long reviewId, List<Long> deleteImageIds) {

        if (deleteImageIds == null || deleteImageIds.isEmpty()) {
            return List.of();
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new GeneralException(ReviewErrorCode._REVIEW_NOT_FOUND));

        return review.getImages().stream()
                .filter(img -> deleteImageIds.contains(img.getImageId()))
                .map(ReviewImage::getImageUrl)
                .toList();
    }


    @Transactional
    public void updateReviewData(ReviewUpdateCommand command, List<String> newS3Urls) {

        Review review = reviewRepository.findById(command.reviewId())
                .orElseThrow(() -> new GeneralException(ReviewErrorCode._REVIEW_NOT_FOUND));

        if (!review.getUser().getUserId().equals(command.userId())) {
            throw new GeneralException(ReviewErrorCode._REVIEW_USER_NOT_ROLE);
        }

        int currentCount = review.getImages().size();
        int deleteCount = (command.deleteImageIds() != null) ? command.deleteImageIds().size() : 0;
        int newCount = newS3Urls.size();

        if (currentCount - deleteCount + newCount > 4) {
            throw new GeneralException(ReviewErrorCode._TOO_MANY_REVIEW_IMAGES);
        }

        if (command.content() != null) {
            review.updateContent(command.content(), LocalDateTime.now());
        }

        if (command.deleteImageIds() != null && !command.deleteImageIds().isEmpty()) {
            review.getImages().removeIf(img -> command.deleteImageIds().contains(img.getImageId()));
        }

        if (!newS3Urls.isEmpty()) {
            review.addImages(newS3Urls);
        }
    }

    @Transactional
    public List<String> deleteAndGetUrls(Long reviewId, Long userId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new GeneralException(ReviewErrorCode._REVIEW_NOT_FOUND));

        if (!review.getUser().getUserId().equals(userId)) {
            throw new GeneralException(ReviewErrorCode._REVIEW_USER_NOT_ROLE);
        }

        if (review.getImages() == null || review.getImages().isEmpty()) {
            return List.of();
        }
        reviewRepository.delete(review);

        return review.getImages().stream()
                .map(ReviewImage::getImageUrl)
                .toList();
    }

}