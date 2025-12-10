CREATE TABLE `device_token` (
    `token_id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `token` VARCHAR(255) NOT NULL,
    `platform` ENUM('ANDROID','IOS') NOT NULL,
    `active` BOOLEAN DEFAULT TRUE,
    `last_active_at` DATETIME DEFAULT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
        PRIMARY KEY (`token_id`),
        CONSTRAINT `fk_device_user` FOREIGN KEY (`user_id`)
            REFERENCES `user`(`user_id`)
            ON DELETE CASCADE
);

CREATE TABLE `topic_token` (
    `topic_id` BIGINT NOT NULL AUTO_INCREMENT,
    `device_token_id` BIGINT NOT NULL,
    `topic` VARCHAR(50) NOT NULL,
        PRIMARY KEY (`topic_id`),
        CONSTRAINT `fk_topic_device` FOREIGN KEY (`device_token_id`)
            REFERENCES `device_token`(`token_id`)
            ON DELETE CASCADE
);