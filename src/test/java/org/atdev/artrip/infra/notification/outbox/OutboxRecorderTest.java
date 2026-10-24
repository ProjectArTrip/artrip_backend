package org.atdev.artrip.infra.notification.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.atdev.artrip.domain.exhibit.event.ExhibitCreatedEvent;
import org.atdev.artrip.repository.NotificationOutboxRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OutboxRecorderTest {

    private final NotificationOutboxRepository outboxRepository = mock(NotificationOutboxRepository.class);
    private final ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);
    private final OutboxPayloadMapper payloadMapper = new OutboxPayloadMapper(new ObjectMapper());
    private final OutboxRecorder recorder = new OutboxRecorder(outboxRepository, payloadMapper, publisher);

    @Test
    @DisplayName("이벤트를 PENDING outbox로 저장하고, 저장된 이벤트가 그대로 복원된다")
    void recordAndRestore() {
        ExhibitCreatedEvent event = new ExhibitCreatedEvent(List.of(
                new ExhibitCreatedEvent.ExhibitSummary(1L, "전시", "미술관", Set.of(3L, 4L)),
                new ExhibitCreatedEvent.ExhibitSummary(2L, "전시2", "미술관2", Set.of())));
        when(outboxRepository.save(any())).thenAnswer(invocation -> {
            NotificationOutbox saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "outboxId", 7L);
            return saved;
        });

        recorder.record(event);

        ArgumentCaptor<NotificationOutbox> captor = ArgumentCaptor.forClass(NotificationOutbox.class);
        verify(outboxRepository).save(captor.capture());
        NotificationOutbox saved = captor.getValue();

        assertThat(saved.getEventType()).isEqualTo(OutboxEventType.EXHIBIT_CREATED);
        assertThat(saved.getStatus()).isEqualTo(OutboxStatus.PENDING);
        assertThat(payloadMapper.read(saved.getEventType(), saved.getPayload())).isEqualTo(event);
        verify(publisher).publishEvent(new OutboxRecordedEvent(7L));
    }
}
