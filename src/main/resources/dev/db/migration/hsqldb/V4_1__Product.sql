create table product (
    id VARCHAR(256) PRIMARY KEY,
    code integer not null,
    name varchar(50) not  null,
    type varchar(9) not null,
    issuer_id varchar(256) not null,
    payment_rule_group_id varchar(256) not null,
    accredited_network_id varchar(256) not null,
    payment_instrument_type varchar(100) not null,
    credit_insertion_type varchar(100) not null,
    mininum_credit_insertion decimal(20,2),
    maximum_credit_insertion decimal(20,2),
    payment_instrument_valid_days integer not null,
    situation char(1) not null,
    membership_fee decimal(20,2) not null,
    credit_insertion_fee decimal(20,2) not null,
    payment_instrument_emission_fee decimal(20,2) not null,
    payment_instrument_second_copy_fee decimal(20,2) not null,
    administration_credit_insertion_fee decimal(2,4) not null,
    version integer,
    constraint fk_product_issuer foreign key(issuer_id) references issuer(id),
    constraint fk_product_accredited_net foreign key(accredited_network_id) references accredited_network(id),
    constraint fk_product_pay_rule_group foreign key(payment_rule_group_id) references payment_rule_group(id)
);

create table product_service (
    product_id varchar(256) not null,
    service_id varchar(256) not null,
    constraint fk_prod_prod_service foreign key(product_id) references product(id),
    constraint fk_serv_prod_service foreign key(service_id) references service(id)
);

insert into AUTHORITY(name, description) values('ROLE_LIST_PRODUCT','Permite listar Produtos');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_PRODUCT','Permite gerenciar Produtos');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_PRODUCT', '1');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_PRODUCT', '1');

