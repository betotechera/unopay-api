create table complementary_travel_document (
    id VARCHAR(256) PRIMARY KEY,
    quantity integer,
    type varchar(150),
    abbreviation varchar(100),
    document_number varchar(256),
    situation varchar(100),
    caveat varchar(100),
    create_date_time timestamp not null,
    delivery_date_time timestamp,
    receipt_situation varchar(100) not null,
    reason_receipt_situation varchar(100) not null,
    version integer
);

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
    complementary_document_id varchar(256),
    version integer,
    constraint fk_trav_doc_contract foreign key(contract_id) references contract(id),
    constraint fk_trav_doc_compl foreign key(complementary_document_id) references complementary_travel_document(id)
);

create table cargo_contract (
    id VARCHAR(256) PRIMARY KEY,
    contract_id varchar(256) not null,
    caveat varchar(100) not null,
    cargo_profile varchar(100) not null,
    receipt_observation varchar(256),
    cargo_weight decimal(20,2),
    damaged_itens varchar(256),
    receipt_step varchar(100),
    create_date_time timestamp not null,
    version integer,
    constraint fk_cargo_contract foreign key(contract_id) references contract(id)
);


insert into AUTHORITY(name, description) values('ROLE_LIST_CARGO_CONTRACT','Permite listar contrato de carga.');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_CARGO_CONTRACT','Permite gerenciar contrato de carga.');

insert into AUTHORITY(name, description) values('ROLE_LIST_TRAVEL_DOCUMENT','Permite listar documentos da viagem.');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_TRAVEL_DOCUMENT','Permite gerenciar documentos da viagem.');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_TRAVEL_DOCUMENT', '1');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_TRAVEL_DOCUMENT', '1');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_CARGO_CONTRACT', '1');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_CARGO_CONTRACT', '1');