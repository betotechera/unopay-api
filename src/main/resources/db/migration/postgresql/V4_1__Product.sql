create table product (
    id VARCHAR(256) PRIMARY KEY,
    code varchar(4) not null,
    name varchar(50) not  null,
    type varchar(9) not null,
    issuer_id varchar(256) not null,
    payment_rule_group_id varchar(256) not null,
    accredited_network_id varchar(256) not null,
    payment_instrument_type varchar(100) not null,
    credit_insertion_type varchar(100) not null,
    minimum_credit_insertion decimal(20,2),
    maximum_credit_insertion decimal(20,2),
    payment_instrument_valid_days integer not null,
    situation varchar(50) not null,
    membership_fee decimal(20,2) not null,
    credit_insertion_fee decimal(20,2) not null,
    pay_inst_emission_fee decimal(20,2) not null,
    pay_inst_second_copy_fee decimal(20,2) not null,
    adm_credit_insert_fee decimal(20,4) not null,
    version integer,
    CONSTRAINT product_name UNIQUE (name),
    CONSTRAINT product_code UNIQUE (code),
    constraint fk_prd_issuer foreign key(issuer_id) references issuer(id),
    constraint fk_prd_accredited_net foreign key(accredited_network_id) references accredited_network(id),
    constraint fk_prod_pay_rule_group foreign key(payment_rule_group_id) references payment_rule_group(id)
);

create table product_service_type (
    product_id varchar(256) not null,
    service_type varchar(256) not null,
    constraint fk_prod_prod_service foreign key(product_id) references product(id)
);

insert into AUTHORITY(name, description) values('ROLE_LIST_PRODUCT','Permite listar Produtos');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_PRODUCT','Permite gerenciar Produtos');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_PRODUCT', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_PRODUCT', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');


