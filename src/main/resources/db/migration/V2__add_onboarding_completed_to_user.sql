ALTER TABLE `user`
    ADD COLUMN `onboarding_completed` TINYINT(1)
NOT NULL DEFAULT 0;