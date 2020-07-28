CREATE TABLE LSMSISDN (
id number not null constraint lsmsisdn_pk primary key,
msisdn varchar(10) not null constraint msisdn_uc unique,
accountId varchar(8) not null constraint accountId_uc unique,
status varchar(255) not null
);

create sequence
 lsmsisdn_pk_seq
increment by 1
start with 1;

CREATE TRIGGER lsmsisdn_before
BEFORE INSERT ON LSMSISDN
FOR EACH ROW
BEGIN
SELECT lsmsisdn_pk_seq.nextval
INTO :new.id
FROM dual;
END;