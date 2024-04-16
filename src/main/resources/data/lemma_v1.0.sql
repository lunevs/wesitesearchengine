--liquibase formatted sql

--changeset LEMMA:0 author:LunevS
create table lemma (
        id              int primary key AUTO_INCREMENT,
        site_id         int not null,
        lemma           varchar(255) not null,
        frequency       int not null
);
--rollback drop table lemma;


