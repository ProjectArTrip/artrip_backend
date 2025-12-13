alter table exhibit_hall add Column longitude DECIMAL(10, 7) NULL;
alter table exhibit_hall add Column latitude DECIMAL(10, 7) NULL;

alter table exhibit drop Column longitude;
alter table exhibit drop Column latitude;

alter table keyword drop column `group`
