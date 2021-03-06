create table "order" (
    id VARCHAR(256) PRIMARY KEY,
    order_number varchar(100) not null,
    product_id varchar(256) not null,
    person_id VARCHAR(256) not null,
    payment_instrument_id varchar(256),
    create_date_time TIMESTAMP not null,
    status varchar(150) not null,
    value decimal(20,2) not null,
    contract_id varchar(256),
    partner_id varchar(256),
    type varchar(150) not null,
    version integer,
    constraint fk_order_person foreign key(person_id) references person(id),
    constraint fk_order_product foreign key(product_id) references product(id),
    constraint fk_instrument_order foreign key(payment_instrument_id) references payment_instrument(id),
    constraint fk_contract_order foreign key(contract_id) references contract(id)
);

create table transaction (
    id VARCHAR(256) PRIMARY KEY,
    order_id VARCHAR(256) not null,
    payment_method varchar(100) not null,
    status varchar(100) not null,
    currency varchar(5) not null,
    value decimal(20,2) not null,
    installments integer not null,
    create_date_time Timestamp not null,
    captured_requested_at Timestamp,
    cancellation_requested_at Timestamp,
    version integer
);

alter table contractor_instrument_credit drop column credit_insertion_type;
alter table contractor_instrument_credit add credit_source varchar(100) default 'HIRER_CREDIT' not null;
alter table contractor_instrument_credit alter column service_type type varchar(150);
alter table contractor_instrument_credit alter column service_type drop not null;

insert into AUTHORITY(name, description) values('ROLE_LIST_ORDERS','Permite listar pedidos.');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_ORDERS','Permite gerenciar pedidos');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_ORDERS', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_ORDERS', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');