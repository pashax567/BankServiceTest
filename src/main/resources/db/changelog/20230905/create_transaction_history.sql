create table TRANSACTION_HISTORY
(
    ID               BIGINT auto_increment
        primary key,
    AMOUNT           BIGINT                 not null,
    OPERATION_TYPE   CHARACTER VARYING(255) not null,
    TRANSACTION_DATE TIMESTAMP              not null,
    ACCOUNT_FROM     BIGINT,
    ACCOUNT_TO       BIGINT,
    constraint FKFODW7U2KI6ISD9LGE140PB172
        foreign key (ACCOUNT_FROM) references ACCOUNT,
    constraint FKS3W39EPAGGYF23WJSSN5AUJEL
        foreign key (ACCOUNT_TO) references ACCOUNT,
    check ("OPERATION_TYPE" IN ('DEPOSIT', 'WITHDRAW', 'TRANSFER')),
    check ("AMOUNT" > 0)
);

