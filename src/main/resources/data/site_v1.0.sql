--liquibase formatted sql

--changeset SITE:0 author:LunevS
create table site (
        id              int primary key AUTO_INCREMENT,
        status          enum('INDEXING', 'INDEXED', 'FAILED') not null,
        status_time     datetime not null,
        last_error      text,
        url             varchar(255) not null,
        name            varchar(255) not null
);
--rollback drop table site;


