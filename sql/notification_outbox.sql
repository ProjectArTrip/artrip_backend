-- 알림 outbox 테이블. 운영(ddl-auto: validate) 배포 전에 먼저 실행해야 한다.
CREATE TABLE notification_outbox (
    outbox_id     BIGINT        NOT NULL AUTO_INCREMENT,
    event_type    VARCHAR(50)   NOT NULL,
    payload       JSON          NOT NULL,
    status        VARCHAR(20)   NOT NULL,
    inbox_saved   BIT(1)        NOT NULL DEFAULT b'0',
    push_cursor   BIGINT        NULL,
    retry_count   INT           NOT NULL DEFAULT 0,
    next_retry_at DATETIME(6)   NOT NULL,
    locked_until  DATETIME(6)   NULL,
    last_error    VARCHAR(1000) NULL,
    created_at    DATETIME(6)   NOT NULL,
    processed_at  DATETIME(6)   NULL,
    PRIMARY KEY (outbox_id),
    INDEX idx_outbox_poll (status, next_retry_at)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
