package org.atdev.artrip.infra.notification.outbox;

public record OutboxRecordedEvent(
        Long outboxId
) {
}
