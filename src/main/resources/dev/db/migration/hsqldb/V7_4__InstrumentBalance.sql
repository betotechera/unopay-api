create table instrument_balance (
    id VARCHAR(256) PRIMARY KEY,
    payment_instrument_id VARCHAR(256) not null,
    document_number varchar(256) not null,
    value decimal(20,2) not null,
    created_date_time timestamp not null,
    updated_date_time timestamp not null,
    version integer
);

insert into AUTHORITY(name, description) values('ROLE_LIST_INSTRUMENT_BALANCE','Permite listar saldos dos instrumentos.');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_INSTRUMENT_BALANCE','Permite gerenciar saldos dos instrumentos');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_INSTRUMENT_BALANCE', '1');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_INSTRUMENT_BALANCE', '1');

