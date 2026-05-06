package org.atdev.artrip.service;

import org.atdev.artrip.constants.ReviewRejectionReason;
import org.atdev.artrip.constants.ReviewStatus;
import org.atdev.artrip.constants.Role;
import org.atdev.artrip.constants.Status;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.domain.exhibitHall.ExhibitHall;
import org.atdev.artrip.domain.review.Review;
import org.atdev.artrip.global.apipayload.code.error.ReviewErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.infra.fcm.service.event.ReviewApprovedEvent;
import org.atdev.artrip.infra.fcm.service.event.ReviewDeleteByAdminEvent;
import org.atdev.artrip.repository.ReviewRepository;
import org.atdev.artrip.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminReviewServiceTest {

    @Mock ReviewRepository reviewRepository;
    @Mock UserRepository userRepository;
    @Mock ApplicationEventPublisher eventPublisher;

    @InjectMocks
    AdminReviewService adminReviewService;

    private User admin;
    private User reviewer;
    private Exhibit exhibit;

    @BeforeEach
    void setUp() {
        admin = User.builder().userId(1L).role(Role.ADMIN).build();
        reviewer = User.builder().userId(2L).role(Role.USER).build();

        ExhibitHall hall = ExhibitHall.of(10L, "다빈치홀", "프랑스", "파리", Boolean.FALSE);
        exhibit = Exhibit.of(100L, "다빈치 전", hall, Status.ONGOING,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(30));
    }

    @Test
    @DisplayName("관리자가 PENDING 리뷰를 승인하면 APPROVED + 이벤트 발행")
    void approve_pending_publishesEvent() {
        //given
        Long reviewId = 500L;
        Review review = Review.create(reviewer, exhibit, "리뷰", LocalDate.now(), List.of());
        when(userRepository.findById(admin.getUserId())).thenReturn(Optional.of(admin));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        //when
        adminReviewService.approveReview(admin.getUserId(), reviewId);

        //then
        assertThat(review.getStatus()).isEqualTo(ReviewStatus.APPROVED);
        verify(eventPublisher).publishEvent(new ReviewApprovedEvent(
                review.getReviewId(),
                reviewer.getUserId(),
                exhibit.getExhibitId(),
                exhibit.getTitle()));
    }

    @Test
    @DisplayName("이미 승인된 리뷰는 다시 승인 불가")
    void approve_alreadyApproved_throws() {
        //given
        Long reviewId = 510L;
        Review review = Review.create(reviewer, exhibit, "리뷰", LocalDate.now(), List.of());
        review.approve();

        when(userRepository.findById(admin.getUserId())).thenReturn(Optional.of(admin));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        //when
        //then
        assertThatThrownBy(() -> adminReviewService.approveReview(admin.getUserId(), reviewId))
                .isInstanceOfSatisfying(GeneralException.class,
                        ex -> assertThat(ex.getCode()).isEqualTo(ReviewErrorCode._REVIEW_ALREADY_APPROVED));
        verifyNoInteractions(eventPublisher);
    }

    @Test
    @DisplayName("이미 반려된 리뷰는 다시 반려 불가")
    void reject_alreadyRejected_throws() {
        //given
        Long reviewId = 700L;
        Review review = Review.create(reviewer, exhibit, "리뷰", LocalDate.now(), List.of());
        review.reject(ReviewRejectionReason.POLICY_VIOLATION);
        when(userRepository.findById(admin.getUserId())).thenReturn(Optional.of(admin));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        //when
        //then
        assertThatThrownBy(() -> adminReviewService.rejectReview(
                admin.getUserId(), reviewId, ReviewRejectionReason.BANNED_WORD))
                .isInstanceOfSatisfying(GeneralException.class,
                        ex -> assertThat(ex.getCode()).isEqualTo(ReviewErrorCode._REVIEW_ALREADY_REJECTED));
        verifyNoInteractions(eventPublisher);
    }

    @Test
    @DisplayName("관리자가 리뷰 삭제 시 이벤트 발행")
    void delete_publishesEvent() {
        //given
        Long reviewId = 800L;
        Review review = Review.create(reviewer, exhibit, "리뷰", LocalDate.now(), List.of());
        when(userRepository.findById(admin.getUserId())).thenReturn(Optional.of(admin));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        //when
        adminReviewService.delete(admin.getUserId(), reviewId);

        //then
        verify(reviewRepository).delete(review);
        verify(eventPublisher).publishEvent(
                new ReviewDeleteByAdminEvent(reviewId, reviewer.getUserId(), exhibit.getTitle()));
    }

}
