--liquibase formatted sql

--changeset SITE:1 author:LunevS
alter table site add unique key (url);
--rollback alter table site drop unique key (url);


