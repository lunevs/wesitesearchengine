--liquibase formatted sql

--changeset LEMMA:1 author:LunevS
alter table lemma add unique key (site_id,lemma);
--rollback alter table page drop unique key (site_id,lemma);


