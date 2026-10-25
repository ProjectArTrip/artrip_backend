package org.atdev.artrip.infra.notification.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class OutboxPayloadMapper {

    private final ObjectMapper objectMapper;

    public OutboxPayloadMapper(ObjectMapper objectMapper) {
        // 이벤트 record의 isXxx() 같은 계산 메서드도 직렬화되므로, 역직렬화 시 모르는 필드는 무시한다
        this.objectMapper = objectMapper.copy()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public String write(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("outbox payload 직렬화 실패: " + event.getClass().getName(), e);
        }
    }

    public Object read(OutboxEventType type, String payload) {
        try {
            return objectMapper.readValue(payload, type.getEventClass());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("outbox payload 역직렬화 실패: " + type, e);
        }
    }
}
