create table credit (
    id VARCHAR(256) PRIMARY KEY,
    product_id varchar(256),
    payment_rule_group_id varchar(256) not null,
    hirer_document varchar(20) not null,
    service_type varchar(100),
    credit_insertion_type varchar(100) not null,
    credit_number bigint not null,
    created_date_time TIMESTAMP not null,
    value decimal(20,2) not null,
    situation varchar(50) not null,
    credit_source varchar(256) not null,
    cnab_id varchar(256),
    available_value decimal(20,2) not null,
    blocked_value decimal(20,2) not null,
    version integer,
    constraint fk_credit_product foreign key(product_id) references product(id),
    constraint fk_credit_pay_rule_group foreign key(payment_rule_group_id) references payment_rule_group(id)
);

create table payment_account (
    id VARCHAR(256) PRIMARY KEY,
    transaction_created_date_time TIMESTAMP not null,
    issuer_id varchar(256) not null,
    product_id varchar(256),
    payment_rule_group_id varchar(256) not null,
    hirer_document varchar(20) not null,
    service_type varchar(100),
    credit_insertion_type varchar(100) not null,
    solicitation_date_time TIMESTAMP not null,
    credit_number bigint not null,
    insertion_created_date_time TIMESTAMP not null,
    value decimal(20,2) not null,
    situation varchar(50) not null,
    credit_source varchar(256) not null,
    cnab_id varchar(256),
    PAYMENT_BANK_ACCOUNT_ID varchar(256),
    available_balance decimal(20,2) not null,
    version integer,
    constraint fk_h_cred_issuer foreign key(issuer_id) references issuer(id),
    constraint fk_h_cred_product foreign key(product_id) references product(id),
    constraint fk_h_cred_account foreign key(PAYMENT_BANK_ACCOUNT_ID) references payment_bank_account(id)
);

insert into AUTHORITY(name, description) values('ROLE_LIST_CREDIT','Permite listar Creditos.');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_CREDIT','Permite gerenciar Creditos.');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_CREDIT', '1');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_CREDIT', '1');

insert into AUTHORITY(name, description) values('ROLE_LIST_HIRER_CREDIT','Permite listar Conta de Pagamento.');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_HIRER_CREDIT','Permite gerenciar Conta de Pagamento.');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_HIRER_CREDIT', '1');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_HIRER_CREDIT', '1');