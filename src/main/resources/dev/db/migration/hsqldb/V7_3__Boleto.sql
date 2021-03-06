create table boleto (
    id VARCHAR(256) PRIMARY KEY,
    order_id VARCHAR(256) not null,
    issuer_document varchar(256) not null,
    client_document varchar(256) not null,
    "number" varchar(100) not null,
    our_number varchar(20) not null,
    expiration_date_time timestamp not null,
    processed_at timestamp not null,
    value decimal(20,2) not null,
    payment_penalty_value decimal(20,2),
    interest decimal(3,2),
    uri varchar(256) not null,
    typing_code varchar(256) not null,
    create_date_time Timestamp not null,
    version integer
);

insert into AUTHORITY(name, description) values('ROLE_LIST_BOLETOS','Permite listar boletos.');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_BOLETOS','Permite gerenciar boletos');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_BOLETOS', '1');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_BOLETOS', '1');

alter table payment_bank_account add station varchar(100) default '0000000' not null;
alter table payment_bank_account add wallet_number varchar(100) default '000' not null;
alter table contract add created_date_time timestamp default SYSDATE() not null;