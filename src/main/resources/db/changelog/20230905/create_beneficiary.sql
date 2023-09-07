create table BENEFICIARY
(
    ID       BIGINT auto_increment
        primary key,
    USERNAME CHARACTER VARYING(255) not null
        constraint UK_82JBOLV5QDYBBBP680CST7JD2
            unique
);