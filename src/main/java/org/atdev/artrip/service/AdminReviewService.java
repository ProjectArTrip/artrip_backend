package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.constants.ReviewStatus;
import org.atdev.artrip.constants.Role;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.review.Review;
import org.atdev.artrip.global.apipayload.code.error.ReviewErrorCode;
import org.atdev.artrip.global.apipayload.code.error.UserErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.domain.review.event.ReviewApprovedEvent;
import org.atdev.artrip.domain.review.event.ReviewDeleteByAdminEvent;
import org.atdev.artrip.domain.review.event.ReviewRejectedEvent;
import org.atdev.artrip.repository.ReviewRepository;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.service.dto.command.AdminReviewRejectCommand;
import org.atdev.artrip.service.dto.command.AdminReviewSearchCommand;
import org.atdev.artrip.service.dto.result.AdminReviewListResult;
import org.atdev.artrip.service.dto.result.AdminReviewResult;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminReviewService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional(readOnly = true)
    public AdminReviewListResult list(Long adminId, AdminReviewSearchCommand command, Pageable pageable) {
        findAdminOrThrow(adminId);
        Page<AdminReviewResult> page = reviewRepository.searchForAdmin(command, pageable);
        return AdminReviewListResult.from(page);
    }

    @Transactional(readOnly = true)
    public AdminReviewResult detail(Long adminId, Long reviewId) {
        findAdminOrThrow(adminId);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new GeneralException(ReviewErrorCode._REVIEW_NOT_FOUND));
        return AdminReviewResult.from(review);
    }

    @Transactional
    public void approveReview(Long adminId, Long reviewId) {
        findAdminOrThrow(adminId);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new GeneralException(ReviewErrorCode._REVIEW_NOT_FOUND));

        if (review.getStatus() == ReviewStatus.APPROVED) {
            throw new GeneralException(ReviewErrorCode._REVIEW_ALREADY_APPROVED);
        }
        if (review.getStatus() != ReviewStatus.PENDING) {
            throw new GeneralException(ReviewErrorCode._REVIEW_NOT_PENDING);
        }

        review.approve();

        publisher.publishEvent(new ReviewApprovedEvent(
                review.getReviewId(),
                review.getUser().getUserId(),
                review.getExhibit().getExhibitId(),
                review.getExhibit().getTitle()
        ));
    }

    @Transactional
    public void delete(Long adminId, Long reviewId) {
        findAdminOrThrow(adminId);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new GeneralException(ReviewErrorCode._REVIEW_NOT_FOUND));
        Long ownerId = review.getUser().getUserId();
        String exhibitTitle = review.getExhibit().getTitle();

        reviewRepository.delete(review);

        publisher.publishEvent(new ReviewDeleteByAdminEvent(reviewId, ownerId, exhibitTitle));
    }

    @Transactional
    public void rejectReview(AdminReviewRejectCommand command) {
        findAdminOrThrow(command.adminId());
        Review review = reviewRepository.findById(command.reviewId())
                .orElseThrow(() -> new GeneralException(ReviewErrorCode._REVIEW_NOT_FOUND));

        if (review.getStatus() == ReviewStatus.REJECTED) {
            throw new GeneralException(ReviewErrorCode._REVIEW_ALREADY_REJECTED);
        }
        if (review.getStatus() != ReviewStatus.PENDING) {
            throw new GeneralException(ReviewErrorCode._REVIEW_NOT_PENDING);
        }

        review.reject(command.rejectionReason());

        publisher.publishEvent(new ReviewRejectedEvent(
                review.getReviewId(),
                review.getUser().getUserId(),
                review.getExhibit().getTitle(),
                review.getContent(),
                command.rejectionReason()
        ));
    }

    private User findAdminOrThrow(Long adminId) {
        User user = userRepository.findById(adminId).orElseThrow(() -> new GeneralException(UserErrorCode._USER_NOT_FOUND));
        if (user.getRole() != Role.ADMIN) {
            throw new GeneralException(UserErrorCode._USER_FORBIDDEN);
        }
        return user;
    }
}
