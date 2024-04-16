--liquibase formatted sql

--changeset SEARCH_INDEX:1 author:LunevS
alter table search_index add unique key (page_id,lemma_id);
--rollback alter table page drop unique key (page_id,lemma_id);


