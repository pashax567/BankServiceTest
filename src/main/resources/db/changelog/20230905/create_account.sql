create table ACCOUNT
(
    ID          BIGINT auto_increment
        primary key,
    BALANCE     BIGINT                 not null,
    NUMBER      BIGINT                 not null,
    PIN_CODE    CHARACTER VARYING(255) not null,
    BENEFICIARY BIGINT                 not null,
    constraint FKAN53LYKJFV914451MVP2PCC6W
        foreign key (BENEFICIARY) references BENEFICIARY,
    check ("BALANCE" >= 0)
);