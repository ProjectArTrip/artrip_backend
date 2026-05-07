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
import org.atdev.artrip.global.apipayload.code.error.UserErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.infra.fcm.service.event.ReviewRejectedEvent;
import org.atdev.artrip.repository.ReviewRepository;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.service.dto.command.AdminReviewRejectCommand;
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
    @DisplayName("일반 유저는 리뷰 승인 및 반려시도 할 경우 예외")
    void approve_nonAdmin_throwsForbidden() {
        //given
        User user = User.builder()
                .userId(3L)
                .role(Role.USER)
                .build();
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));

        //when
        //then
        assertThatThrownBy(() -> adminReviewService.approveReview(user.getUserId(), 500L)).isInstanceOfSatisfying(GeneralException.class,
                ex -> assertThat(ex.getCode()).isEqualTo(UserErrorCode._USER_FORBIDDEN));
        verifyNoInteractions(reviewRepository, eventPublisher);
    }

    @Test
    @DisplayName("PENDING 리뷰를 반려하면 REJECTED로 변경되고 이벤트 발행")
    void reject_pending_changesStatus_andPublishesEvent() {
        // given
        Long reviewId = 700L;
        Review review = Review.create(reviewer, exhibit, "리뷰", LocalDate.now(), List.of());
        AdminReviewRejectCommand command = new AdminReviewRejectCommand(admin.getUserId(), reviewId, ReviewRejectionReason.BANNED_WORD);

        when(userRepository.findById(admin.getUserId())).thenReturn(Optional.of(admin));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        // when
        adminReviewService.rejectReview(command);

        // then
        assertThat(review.getStatus()).isEqualTo(ReviewStatus.REJECTED);
        verify(eventPublisher).publishEvent(any(ReviewRejectedEvent.class));
    }

    @Test
    @DisplayName("이미 반려된 리뷰를 승인하면 NOT_PENDING 예외")
    void approve_rejectedReview_throwsNotPending() {
        // given
        Long reviewId = 701L;
        Review review = Review.create(reviewer, exhibit, "리뷰", LocalDate.now(), List.of());
        review.reject(ReviewRejectionReason.POLICY_VIOLATION);

        when(userRepository.findById(admin.getUserId())).thenReturn(Optional.of(admin));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        // when
        // then
        assertThatThrownBy(() -> adminReviewService.approveReview(admin.getUserId(), reviewId))
                .isInstanceOfSatisfying(GeneralException.class,
                        ex -> assertThat(ex.getCode()).isEqualTo(ReviewErrorCode._REVIEW_NOT_PENDING));

        verifyNoInteractions(eventPublisher);
    }

}
