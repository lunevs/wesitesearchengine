--liquibase formatted sql

--changeset SEARCH_INDEX:0 author:LunevS
create table search_index (
        id              int primary key AUTO_INCREMENT,
        page_id         int not null,
        lemma_id        int not null,
        lemma_rank      float not null
);
--rollback drop table search_index;


