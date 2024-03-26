--liquibase formatted sql

--changeset PAGE:0 author:LunevS
create table page (
    id          int primary key AUTO_INCREMENT,
    site_id     int not null,
    path        varchar(500) not null,
    code        int not null,
    content     mediumtext not null,
    key (path)
);
--rollback drop table page;


