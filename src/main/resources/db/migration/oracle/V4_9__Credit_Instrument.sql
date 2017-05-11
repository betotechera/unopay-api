create table contractor_instrument_credit (
    id VARCHAR(256) PRIMARY KEY,
    payment_instrument_id varchar(256) not null,
    contract_id varchar(256) not null,
    service_type varchar(100) not null,
    credit_insertion_type varchar(100) not null,
    installment_number integer not null,
    value decimal(*,2) not null,
    expiration_date_time TIMESTAMP not null,
    issuer_fee decimal(*,2) not null,
    situation varchar(50) not null,
    credit_payment_account_id varchar(256) not null,
    available_balance decimal(*,2) not null,
    blocked_balance decimal(*,2) not null,
    created_date_time TIMESTAMP not null,
    version integer,
    constraint fk_cic_pi foreign key(payment_instrument_id) references payment_instrument(id),
    constraint fk_cic_contract foreign key(contract_id) references contract(id),
    constraint fk_cic_cpa foreign key(credit_payment_account_id) references credit_payment_account(id)
);

insert into AUTHORITY(name, description) values('ROLE_LIST_CREDIT_PAYMENT_INSTRUMENT','Permite listar Credito Instrumento de Pagamentos.');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_CREDIT_PAYMENT_INSTRUMENT','Permite gerenciar Credito Instrumento de Pagamentos.');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_CREDIT_PAYMENT_INSTRUMENT', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_CREDIT_PAYMENT_INSTRUMENT', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
