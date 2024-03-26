--liquibase formatted sql

--changeset PAGE:1 author:LunevS
alter table page add unique key (site_id,path);
--rollback alter table page drop unique key (site_id,path);


