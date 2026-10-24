package org.atdev.artrip.infra.notification.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.atdev.artrip.domain.notice.event.NoticeCreatedEvent;
import org.atdev.artrip.infra.notification.PushProgress;
import org.atdev.artrip.infra.notification.outbox.handler.NotificationHandler;
import org.atdev.artrip.repository.NotificationOutboxRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OutboxProcessorTest {

    private static final Long OUTBOX_ID = 1L;

    @Mock
    private NotificationOutboxRepository outboxRepository;

    @Mock
    private NotificationHandler<NoticeCreatedEvent> handler;

    @Mock
    private PlatformTransactionManager transactionManager;

    private OutboxPayloadMapper payloadMapper;
    private OutboxProcessor processor;
    private NotificationOutbox outbox;

    @BeforeEach
    void setUp() {
        payloadMapper = new OutboxPayloadMapper(new ObjectMapper());
        when(handler.types()).thenReturn(Set.of(OutboxEventType.NOTICE_CREATED));
        processor = new OutboxProcessor(outboxRepository, payloadMapper, List.of(handler), transactionManager);

        outbox = NotificationOutbox.pending(
                OutboxEventType.NOTICE_CREATED,
                payloadMapper.write(new NoticeCreatedEvent(10L, "제목", "내용")),
                LocalDateTime.now());
        ReflectionTestUtils.setField(outbox, "outboxId", OUTBOX_ID);

        when(outboxRepository.claim(eq(OUTBOX_ID), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(true);
        when(outboxRepository.findById(OUTBOX_ID)).thenReturn(Optional.of(outbox));
    }

    @Test
    @DisplayName("선점에 실패하면(다른 워커가 처리 중) 아무것도 하지 않는다")
    void skipWhenNotClaimed() throws Exception {
        when(outboxRepository.claim(eq(OUTBOX_ID), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(false);

        processor.process(OUTBOX_ID);

        verify(handler, never()).saveInbox(any());
        verify(handler, never()).sendPush(any(), any());
    }

    @Test
    @DisplayName("성공하면 알림함 저장 → 푸시 발송 후 SENT가 된다")
    void success() throws Exception {
        processor.process(OUTBOX_ID);

        verify(handler).saveInbox(new NoticeCreatedEvent(10L, "제목", "내용"));
        verify(handler).sendPush(eq(new NoticeCreatedEvent(10L, "제목", "내용")), any());
        assertThat(outbox.isInboxSaved()).isTrue();
        assertThat(outbox.getStatus()).isEqualTo(OutboxStatus.SENT);
        assertThat(outbox.getProcessedAt()).isNotNull();
    }

    @Test
    @DisplayName("재시도 시 알림함이 이미 저장됐으면 저장을 건너뛰고 커서부터 이어서 보낸다")
    void resumeFromCursor() throws Exception {
        outbox.markInboxSaved();
        outbox.advancePushCursor(500L, LocalDateTime.now());

        doAnswer(invocation -> {
            PushProgress progress = invocation.getArgument(1);
            assertThat(progress.cursor()).isEqualTo(500L);
            progress.advance(800L);
            return null;
        }).when(handler).sendPush(any(), any());

        processor.process(OUTBOX_ID);

        verify(handler, never()).saveInbox(any());
        assertThat(outbox.getPushCursor()).isEqualTo(800L);
        assertThat(outbox.getStatus()).isEqualTo(OutboxStatus.SENT);
    }

    @Test
    @DisplayName("일시적 오류면 retry_count를 올리고 백오프 뒤로 재시도를 예약한다")
    void scheduleRetryOnTransientError() throws Exception {
        doThrow(new QueryTimeoutException("timeout")).when(handler).sendPush(any(), any());

        processor.process(OUTBOX_ID);

        assertThat(outbox.getStatus()).isEqualTo(OutboxStatus.PENDING);
        assertThat(outbox.getRetryCount()).isEqualTo(1);
        assertThat(outbox.getNextRetryAt()).isAfter(LocalDateTime.now().plusSeconds(5));
        assertThat(outbox.getLockedUntil()).isNull();
        assertThat(outbox.isInboxSaved()).isTrue();
        assertThat(outbox.getLastError()).contains("QueryTimeoutException");
    }

    @Test
    @DisplayName("재시도해도 결과가 같은 오류면 바로 FAILED가 된다")
    void failImmediatelyOnPermanentError() throws Exception {
        doThrow(new IllegalStateException("bad data")).when(handler).sendPush(any(), any());

        processor.process(OUTBOX_ID);

        assertThat(outbox.getStatus()).isEqualTo(OutboxStatus.FAILED);
        assertThat(outbox.getRetryCount()).isZero();
    }

    @Test
    @DisplayName("최대 재시도 횟수를 넘기면 FAILED가 된다")
    void failAfterMaxRetry() throws Exception {
        ReflectionTestUtils.setField(outbox, "retryCount", OutboxRetryPolicy.MAX_RETRY);
        doThrow(new QueryTimeoutException("timeout")).when(handler).sendPush(any(), any());

        processor.process(OUTBOX_ID);

        assertThat(outbox.getStatus()).isEqualTo(OutboxStatus.FAILED);
    }

    @Test
    @DisplayName("알림함 저장이 실패하면 inbox_saved는 false로 남는다")
    void inboxSaveFailure() {
        doThrow(new QueryTimeoutException("timeout")).when(handler).saveInbox(any());

        processor.process(OUTBOX_ID);

        assertThat(outbox.isInboxSaved()).isFalse();
        assertThat(outbox.getStatus()).isEqualTo(OutboxStatus.PENDING);
    }
}
