CREATE TABLE LSMSISDN (
msisdn varchar(10) not null constraint msisdn_uc unique,
accountId varchar(8) not null constraint lsmsisdn_pk primary key,
status varchar(255) not null
);