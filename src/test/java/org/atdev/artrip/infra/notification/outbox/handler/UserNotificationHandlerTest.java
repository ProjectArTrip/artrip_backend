package org.atdev.artrip.infra.notification.outbox.handler;

import org.atdev.artrip.constants.NotificationAction;
import org.atdev.artrip.domain.exhibitReport.ExhibitReportCreatedEvent;
import org.atdev.artrip.domain.exhibitReport.ExhibitReportRegisteredEvent;
import org.atdev.artrip.domain.review.event.ReviewApprovedEvent;
import org.atdev.artrip.domain.review.event.ReviewDeleteByAdminEvent;
import org.atdev.artrip.domain.review.event.ReviewRejectedEvent;
import org.atdev.artrip.infra.fcm.FcmNotificationService;
import org.atdev.artrip.infra.notification.NotificationMessage;
import org.atdev.artrip.infra.notification.PushProgress;
import org.atdev.artrip.infra.notification.outbox.OutboxEventType;
import org.atdev.artrip.service.UserNoticeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UserNotificationHandlerTest {

    private final UserNoticeService userNoticeService = mock(UserNoticeService.class);
    private final FcmNotificationService fcmNotificationService = mock(FcmNotificationService.class);
    private final UserNotificationHandler handler = new UserNotificationHandler(userNoticeService, fcmNotificationService);

    static Stream<Arguments> events() {
        return Stream.of(
                Arguments.of(new ReviewApprovedEvent(1L, 10L, 100L, "전시"), NotificationAction.MOVE_EXHIBIT_DETAIL, 100L),
                Arguments.of(new ReviewRejectedEvent(1L, 10L, "전시", "내용", "사유"), NotificationAction.MOVE_REVIEW_EDIT, 1L),
                Arguments.of(new ReviewDeleteByAdminEvent(1L, 10L, "전시"), NotificationAction.NONE, null),
                Arguments.of(new ExhibitReportCreatedEvent(1L, 10L, "제보"), NotificationAction.NONE, null),
                Arguments.of(new ExhibitReportRegisteredEvent(1L, 10L, "제보", 100L), NotificationAction.MOVE_EXHIBIT_DETAIL, 100L)
        );
    }

    @ParameterizedTest
    @MethodSource("events")
    @DisplayName("이벤트별로 대상 사용자와 이동 정보가 맞게 만들어진다")
    void saveInbox(Object event, NotificationAction action, Long referenceId) {
        handler.saveInbox(event);

        ArgumentCaptor<NotificationMessage> captor = ArgumentCaptor.forClass(NotificationMessage.class);
        verify(userNoticeService).saveSingle(eq(10L), captor.capture());
        assertThat(captor.getValue().reference().action()).isEqualTo(action);
        assertThat(captor.getValue().reference().referenceId()).isEqualTo(referenceId);
        assertThat(captor.getValue().body()).isNotBlank();
    }

    @Test
    @DisplayName("단건 알림 5종을 모두 처리한다")
    void types() {
        assertThat(handler.types()).containsExactlyInAnyOrder(
                OutboxEventType.REVIEW_APPROVED,
                OutboxEventType.REVIEW_REJECTED,
                OutboxEventType.REVIEW_DELETED_BY_ADMIN,
                OutboxEventType.EXHIBIT_REPORT_CREATED,
                OutboxEventType.EXHIBIT_REPORT_REGISTERED);
    }

    @Test
    @DisplayName("이미 발송했으면(커서 있음) 다시 보내지 않는다")
    void skipWhenAlreadySent() throws Exception {
        PushProgress progress = mock(PushProgress.class);
        when(progress.cursor()).thenReturn(10L);

        handler.sendPush(new ReviewApprovedEvent(1L, 10L, 100L, "전시"), progress);

        verify(fcmNotificationService, never()).sendToUser(any(), any());
    }

    @Test
    @DisplayName("발송 후 커서를 사용자 id로 전진시킨다")
    void advanceAfterSend() throws Exception {
        PushProgress progress = mock(PushProgress.class);
        when(progress.cursor()).thenReturn(null);

        handler.sendPush(new ExhibitReportCreatedEvent(1L, 10L, "제보"), progress);

        verify(fcmNotificationService).sendToUser(eq(10L), any());
        verify(progress).advance(10L);
    }
}
