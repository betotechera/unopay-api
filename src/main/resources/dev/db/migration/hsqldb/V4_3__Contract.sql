create table contract (
    id VARCHAR(256) PRIMARY KEY,
    code bigint not null,
    name varchar(50) not  null,
    payment_instrument_type varchar(100) not null,
    begin_date TimeStamp,
    end_date TimeStamp,
    credit_insertion_type varchar(50),
    issue_invoice char(1) not null,
    document_number_invoice varchar(50) not null,
    situation varchar(20) not null,
    origin varchar(20) not null,
    product_id varchar(256) not null,
    hirer_id varchar(256) not null,
    contractor_id varchar(256) not null,
    version integer,
    CONSTRAINT contract_code UNIQUE (code),
    constraint fk_contract_product foreign key(product_id) references product(id),
    constraint fk_contract_hirer foreign key(hirer_id) references hirer(id),
    constraint fk_contract_contractor foreign key(contractor_id) references contractor(id)
);

create table contract_service_type (
    contract_id varchar(256) not null,
    service_type varchar(50) not null,
    constraint fk_cs_contract foreign key(contract_id) references contract(id)
);

insert into AUTHORITY(name, description) values('ROLE_LIST_CONTRACT','Permite listar Contratos');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_CONTRACT','Permite gerenciar Contratos');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_CONTRACT', '1');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_CONTRACT', '1');