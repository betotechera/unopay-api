create table contractor_credit_recurrence (
    id VARCHAR(256) PRIMARY KEY,
    hirer_id VARCHAR(256) not null,
    contract_id varchar(256) not null,
    payment_instrument_id varchar(256) not null,
    value decimal(20,2) not null,
    created_date_time Timestamp not null,
    version integer,
    constraint fk_credit_rec_hirer foreign key(hirer_id) references hirer(id),
    constraint fk_credit_rec_contract foreign key(contract_id) references contract(id),
    constraint fk_credit_rec_instrument foreign key(payment_instrument_id) references payment_instrument(id)
);

insert into AUTHORITY(name, description) values('ROLE_LIST_CONTRACTOR_CREDIT_RECURRENCE','Permite listar recorrencia de credito do favorecido.');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_CONTRACTOR_CREDIT_RECURRENCE','Permite gerenciar recorrencia de credito do favorecido');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_CONTRACTOR_CREDIT_RECURRENCE', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_CONTRACTOR_CREDIT_RECURRENCE', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');

alter table credit_payment_account drop credit_number;
alter table hirer add credit_recurrence_period varchar(100) default 'MONTHLY' not null;
alter table hirer add default_credit_value decimal default 0.0 not null;
alter table hirer add default_member_credit_value decimal default 0.0 not null;