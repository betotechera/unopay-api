create table cargo_contract (
    id VARCHAR(256) PRIMARY KEY,
    partner_id varchar(256) not null,
    contract_id varchar(256),
    caveat varchar(100) not null,
    cargo_profile varchar(100) not null,
    receipt_observation varchar(256),
    receipt_step varchar(100),
    payment_source varchar(100),
    travel_situation varchar(100),
    created_date_time timestamp not null,
    receipt_situation varchar(100),
    reason_receipt_situation varchar(100),
    version integer,
    constraint fk_cargo_contract foreign key(contract_id) references contract(id)
);

create table complementary_travel_document (
    id VARCHAR(256) PRIMARY KEY,
    quantity integer,
    type varchar(150),
    document_number varchar(256),
    situation varchar(100),
    caveat varchar(100),
    created_date_time timestamp not null,
    delivery_date_time timestamp,
    cargo_contract_id varchar(256),
    version integer,
     constraint fk_compl_cargo foreign key(cargo_contract_id) references cargo_contract(id)
);

create table travel_document (
    id VARCHAR(256) PRIMARY KEY,
    contract_id varchar(256),
    quantity integer,
    type varchar(150) not null,
    document_number varchar(256),
    situation varchar(100),
    caveat varchar(100),
    created_date_time timestamp not null,
    delivery_date_time timestamp,
    cargo_weight decimal(*,2),
    damaged_items integer,
    version integer,
    cargo_contract_id varchar(256),
    constraint fk_trav_contract foreign key(contract_id) references contract(id),
    constraint fk_trav_cargo foreign key(cargo_contract_id) references cargo_contract(id)
);

insert into AUTHORITY(name, description) values('ROLE_LIST_CARGO_CONTRACT','Permite listar contrato de carga.');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_CARGO_CONTRACT','Permite gerenciar contrato de carga.');

insert into AUTHORITY(name, description) values('ROLE_LIST_TRAVEL_DOCUMENT','Permite listar documentos da viagem.');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_TRAVEL_DOCUMENT','Permite gerenciar documentos da viagem.');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_TRAVEL_DOCUMENT', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_TRAVEL_DOCUMENT', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_CARGO_CONTRACT', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_CARGO_CONTRACT', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');