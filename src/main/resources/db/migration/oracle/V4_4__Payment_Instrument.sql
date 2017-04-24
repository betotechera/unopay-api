create table payment_instrument (
    id VARCHAR(256) PRIMARY KEY,
    type varchar(9) not null,
    number varchar(20) not null,
    product_id varchar(256) not null,
    contractor_id varchar(256) not null,
    created_date DATE not null,
    expiration_date DATE not null,
    password varchar(100),
    situation varchar(100) not null,
    external_number_id varchar(256),
    version integer,
    CONSTRAINT product_external_id UNIQUE (external_number_id),
    constraint fk_instrument_product foreign key(product_id) references product(id),
    constraint fk_instrument_contractor foreign key(contractor_id) references contractor(id)
);

insert into AUTHORITY(name, description) values('ROLE_LIST_PAYMENT_INSTRUMENT','Permite listar Instrumentos de Pagamento');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_PAYMENT_INSTRUMENT','Permite gerenciar Instrumentos de Pagamento');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_PAYMENT_INSTRUMENT', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_PAYMENT_INSTRUMENT', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');