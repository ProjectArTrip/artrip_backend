CREATE TABLE curation
(
    curation_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(255) NOT NULL,
    sub_description VARCHAR(255) ,
    curation_type   VARCHAR(50)  NOT NULL ,
    is_active       BOOLEAN DEFAULT TRUE
);
CREATE TABLE curation_exhibit
(
    curation_id BIGINT NOT NULL,
    exhibit_id  BIGINT NOT NULL,
    PRIMARY KEY (curation_id, exhibit_id),

    CONSTRAINT fk_curation_exhibit_curation
        FOREIGN KEY (curation_id) REFERENCES curation (curation_id)
            ON DELETE CASCADE,

    CONSTRAINT fk_curation_exhibit_exhibit
        FOREIGN KEY (exhibit_id) REFERENCES exhibit (exhibit_id)
            ON DELETE CASCADE
);

CREATE INDEX idx_curation_exhibit_exhibit_id ON curation_exhibit(exhibit_id);