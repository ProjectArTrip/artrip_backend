-- Migration: init_database
-- Created: 2025-11-16
--
-- ========================================
-- Add your DDL statements below
-- ========================================
-- --
-- Example: Create table
-- CREATE TABLE example (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     name VARCHAR(255) NOT NULL,
--     created_at DATETIME(6) NOT NULL
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
-- --
-- Example: Add column
-- ALTER TABLE example ADD COLUMN email VARCHAR(255) NULL;

-- Example: Create index
-- CREATE INDEX idx_example_name ON example(name);

CREATE TABLE `user` (
    `stamp_num` tinyint DEFAULT NULL,
    `created_at` datetime(6) NOT NULL,
    `updated_at` datetime(6) DEFAULT NULL,
    `user_id` bigint NOT NULL AUTO_INCREMENT,
    `email` varchar(255) DEFAULT NULL,
    `name` varchar(255) NOT NULL,
    `push_token` varchar(255) DEFAULT NULL,
    `role` enum('ADMIN','USER') NOT NULL,
    PRIMARY KEY (`user_id`)
);

CREATE TABLE `exhibit_hall` (
    `exhibit_hall_id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL,
    `country` VARCHAR(255) NULL,
    `region` VARCHAR(255) NULL,
    `address` VARCHAR(255) NULL,
    `opening_hours` VARCHAR(255) NULL,
    `closed_days` VARCHAR(255) NULL,
    `phone` VARCHAR(255) NULL,
    `homepage_url` VARCHAR(255) NULL,
    `is_domestic` BIT(1) NULL,
    `created_at` DATETIME(6) NULL,
    `updated_at` DATETIME(6) NULL,

    PRIMARY KEY (`exhibit_hall_id`)
);

CREATE TABLE `keyword` (
    `keyword_id` bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    `type` enum('GENRE','STYLE') NOT NULL,
    `group` varchar(255) NOT NULL,

    PRIMARY KEY (`keyword_id`)
);

CREATE TABLE `exhibit` (
    `exhibit_id` BIGINT NOT NULL AUTO_INCREMENT,
    `exhibit_hall_id` BIGINT NULL,
    `title` VARCHAR(255) NULL,
    `ticket_url` VARCHAR(255) NULL,
    `description` VARCHAR(255) NULL,
    `poster_url` VARCHAR(255) NULL,
    `latitude` DECIMAL(38,2) NULL,
    `longitude` DECIMAL(38,2) NULL,
    `start_date` DATETIME(6) NULL,
    `end_date` DATETIME(6) NULL,
    `created_at` DATETIME(6) NULL,
    `updated_at` DATETIME(6) NULL,

    `status` ENUM('FINISHED', 'ONGOING', 'UPCOMING') NOT NULL,

    PRIMARY KEY (`exhibit_id`),
    KEY `idx_exhibit_hall_id` (`exhibit_hall_id`),
    CONSTRAINT `fk_exhibit_exhibit_hall`
        FOREIGN KEY (`exhibit_hall_id`) REFERENCES `exhibit_hall`(`exhibit_hall_id`)
);

CREATE TABLE `exhibit_keyword` (
    exhibit_id BIGINT NOT NULL,
    keyword_id BIGINT NOT NULL,

    PRIMARY KEY (exhibit_id, keyword_id),
    KEY `idx_keyword_id`(`keyword_id`),
    CONSTRAINT `fk_exhibit_keyword_exhibit`
        FOREIGN KEY (`exhibit_id`) REFERENCES `exhibit` (`exhibit_id`),
    CONSTRAINT `fk_exhibit_keyword_keyword_id`
        FOREIGN KEY (`keyword_id`) REFERENCES keyword(`keyword_id`)
);

CREATE TABLE `favorite_exhibit` (
    `created_at` datetime(6) NOT NULL,
    `exhibit_id` bigint NOT NULL,
    `favorite_id` bigint NOT NULL AUTO_INCREMENT,
    `user_id` bigint NOT NULL,

    PRIMARY KEY (favorite_id),
    KEY `idx_exhibit_id` (`exhibit_id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_favorite_exhibit_user_id`
        FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
    CONSTRAINT `fk_favorite_exhibit_exhibit_id`
        FOREIGN KEY (`exhibit_id`) REFERENCES `exhibit` (`exhibit_id`)
);

CREATE TABLE `notification` (
    `is_read` bit(1) DEFAULT NULL,
    `created_at` datetime(6) NOT NULL,
    `notification_id` bigint NOT NULL AUTO_INCREMENT,
    `user_id` bigint NOT NULL,
    `extra` varchar(255) DEFAULT NULL,
    `message` varchar(255) NOT NULL,
    `type` varchar(255) NOT NULL,

    PRIMARY KEY (`notification_id`),

    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_notification_user_id`
        FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
);

CREATE TABLE `recent_exhibit` (
    `exhibit_id` bigint DEFAULT NULL,
    `recent_exhibit_id` bigint NOT NULL AUTO_INCREMENT,
    `user_id` bigint DEFAULT NULL,
    `view_at` datetime(6) DEFAULT NULL,

    PRIMARY KEY (`recent_exhibit_id`),

    KEY `idx_exhibit_id` (`exhibit_id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_recent_exhibit_exhibit_id`
        FOREIGN KEY (`exhibit_id`) REFERENCES `exhibit` (`exhibit_id`),
    CONSTRAINT `fk_recent_exhibit_user_id`
        FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
);

CREATE TABLE `review` (
    `created_at` datetime(6) NOT NULL,
    `exhibit_id` bigint NOT NULL,
    `review_id` bigint NOT NULL AUTO_INCREMENT,
    `updated_at` datetime(6) DEFAULT NULL,
    `user_id` bigint NOT NULL,
    `visit_date` date DEFAULT NULL,
    `content` varchar(2000) NOT NULL,
    PRIMARY KEY (`review_id`),
    KEY `idx_exhibit_id` (`exhibit_id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_review_exhibit_id`
        FOREIGN KEY (`exhibit_id`) REFERENCES `exhibit` (`exhibit_id`),
    CONSTRAINT `fk_review_user_id`
        FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
);

CREATE TABLE `images` (
    `curation_id` int DEFAULT NULL,
    `created_at` datetime(6) DEFAULT NULL,
    `exhibit_id` bigint DEFAULT NULL,
    `img_id` bigint NOT NULL AUTO_INCREMENT,
    `review_id` bigint DEFAULT NULL,
    `user_id` bigint DEFAULT NULL,
    `image_url` varchar(255) DEFAULT NULL,

    PRIMARY KEY (`img_id`),

    KEY `idx_exhibit_id`(`exhibit_id`),
    KEY `idx_review_id`(`review_id`),
    KEY `idx_user_id`(`user_id`),
    CONSTRAINT `fk_images_user_id`
        FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
    CONSTRAINT `fk_images_review_id`
        FOREIGN KEY (`review_id`) REFERENCES `review` (`review_id`),
    CONSTRAINT `fk_images_exhibit_id`
        FOREIGN KEY (`exhibit_id`) REFERENCES `exhibit` (`exhibit_id`)
);

CREATE TABLE `refresh_token` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `refresh_token` varchar(255) NOT NULL,
    `username` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
);

CREATE TABLE `review_image` (
    `img_id` bigint NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6) DEFAULT NULL,
    `curation_id` int DEFAULT NULL,
    `display_order` int NOT NULL,
    `image_url` varchar(255) DEFAULT NULL,
    `review_id` bigint DEFAULT NULL,
    PRIMARY KEY (`img_id`),
    KEY `idx_review_id` (`review_id`),
    CONSTRAINT `fk_review_images_review_id`
        FOREIGN KEY (`review_id`) REFERENCES `review` (`review_id`)
);

CREATE TABLE `search_history` (
    `created_at` datetime(6) NOT NULL,
    `search_history_id` bigint NOT NULL AUTO_INCREMENT,
    `user_id` bigint NOT NULL,
    `content` varchar(255) NOT NULL,

    PRIMARY KEY (`search_history_id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_search_history_user_id`
        FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
);

CREATE TABLE `social_accounts` (
    `created_at` datetime(6) NOT NULL,
    `social_id` bigint NOT NULL AUTO_INCREMENT,
    `user_id` bigint NOT NULL,
    `access_token` varchar(255) DEFAULT NULL,
    `provider_id` varchar(255) NOT NULL,
    `refresh_token` varchar(255) DEFAULT NULL,
    `provider` enum('APPLE','GOOGLE','KAKAO') NOT NULL,

    PRIMARY KEY (`social_id`),

    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_social_accounts_user_id`
        FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
);

CREATE TABLE `stamp` (
    `acquire_at` datetime(6) NOT NULL,
    `review_id` bigint NOT NULL,
    `stamp_id` bigint NOT NULL AUTO_INCREMENT,
    `user_rank` varchar(255) NOT NULL,
    PRIMARY KEY (`stamp_id`),
    KEY `idx_review_id` (`review_id`),
    CONSTRAINT `fk_stamp_review_id`
        FOREIGN KEY (`review_id`) REFERENCES `review` (`review_id`)
);

CREATE TABLE `user_keyword` (
    `created_at` datetime(6) NOT NULL,
    `id` bigint NOT NULL AUTO_INCREMENT,
    `keyword_id` bigint DEFAULT NULL,
    `user_id` bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_keyword_id` (`keyword_id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_user_keyword_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
    CONSTRAINT `fk_user_keyword_keyword_id` FOREIGN KEY (`keyword_id`) REFERENCES `keyword` (`keyword_id`)
);