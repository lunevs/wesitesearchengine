--liquibase formatted sql

--changeset SITE:2 author:LunevS
ALTER TABLE site MODIFY COLUMN status_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
--rollback ALTER TABLE site MODIFY COLUMN status_time datetime null;


