package db.migration


ALTER TABLE `user`
    ADD COLUMN `onboarding_completed` TINYINT(1)
NOT NULL DEFAULT 0;

UPDATE `user`
SET `onboarding_completed` = 1;
