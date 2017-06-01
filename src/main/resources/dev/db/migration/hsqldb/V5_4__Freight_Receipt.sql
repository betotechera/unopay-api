create table travel_document (
    id VARCHAR(256) PRIMARY KEY,
    contract_id varchar(256) not null,
    quantity integer not null,
    type varchar(150) not null,
    abbreviation varchar(100) not null,
    document_number varchar(256),
    situation varchar(100),
    caveat varchar(100),
    create_date_time timestamp not null,
    delivery_date_time timestamp,
    receipt_situation varchar(100),
    reason_receipt_situation varchar(100),
    complementary_document_quantity integer,
    complementary_document_type varchar(100),
    complementary_document_abbreviation varchar(100),
    complementary_document_create_date_time timestamp,
    complementary_document_delivery_date_time timestamp,
    complementary_document_caveat varchar(100),

    version integer,
    constraint fk_serv_auth_contract foreign key(contract_id) references contract(id)
);

insert into AUTHORITY(name, description) values('ROLE_LIST_TRAVEL_DOCUMENT','Permite listar documentos da viagem.');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_TRAVEL_DOCUMENT','Permite gerenciar documentos da viagem.');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_TRAVEL_DOCUMENT', '1');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_TRAVEL_DOCUMENT', '1');