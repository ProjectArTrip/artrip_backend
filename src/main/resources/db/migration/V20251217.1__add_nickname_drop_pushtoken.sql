ALTER TABLE `user`
DROP COLUMN push_token;

ALTER TABLE `user`
    ADD COLUMN nick_name VARCHAR(50) NULL UNIQUE COMMENT '사용자 닉네임';
