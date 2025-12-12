ALTER TABLE `device_token`
    ADD CONSTRAINT `uk_device_token_token`
        UNIQUE (`token`);

ALTER TABLE `topic_token`
    ADD CONSTRAINT `uk_topic_device_topic`
        UNIQUE (`device_token_id`, `topic`);

