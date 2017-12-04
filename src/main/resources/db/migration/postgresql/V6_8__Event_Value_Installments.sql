alter table person add short_name varchar(50) default 'Nome curto' not null;
alter table product add contract_validity_days integer default 99999 not null;
alter table product add annuity decimal(20,2) default 100.50 not null;
alter table product add payment_installments integer default 10 not null;
alter table establishment drop column brand_flag_id;
alter table establishment add facade_photo_uri varchar(256) null;
alter table event add request_value char(1) default '1' not null;
alter table payment_instrument add gateway_token varchar(256) null;
alter table physical_person_detail add gender varchar(10) default 'MAN' not null;
alter table contract add annuity decimal(20,2) default 100.50 not null;
alter table contract add membership_fee decimal(20,2) null;
alter table contract add payment_installments integer default 10 not null;

create table establishment_event (
    id VARCHAR(256) PRIMARY KEY,
    event_id varchar(256) not null,
    establishment_id varchar(256) not null,
    value decimal(20,2) not null,
    expiration Timestamp not null,
    version integer not null,
    constraint fk_event_value foreign key(event_id) references event(id),
    constraint fk_event_estab foreign key(establishment_id) references establishment(id)
);

create table contract_installment (
    id VARCHAR(256) PRIMARY KEY,
    contract_id varchar(256) not null,
    installment_number integer not null,
    value decimal(20,2) not null,
    expiration Timestamp not null,
    payment_date_time timestamp,
    payment_value decimal(20,2),
    version integer not null,
    constraint fk_contract_install foreign key(contract_id) references contract(id)
);

insert into AUTHORITY(name, description) values('ROLE_LIST_ESTABLISHMENT_EVENT_VALUE','Permite listar valores de eventos do estabelecimento.');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_ESTABLISHMENT_EVENT_VALUE','Permite gerenciar valores de eventos do estabelecimento');
insert into AUTHORITY(name, description) values('ROLE_LIST_ALL_ESTABLISHMENT_EVENT_VALUE','Permite listar valores de eventos de todos os estabelecimentos.');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_ALL_ESTABLISHMENT_EVENT_VALUE','Permite gerenciar valores de eventos de todos os estabelecimentos.');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_ESTABLISHMENT_EVENT_VALUE', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_ESTABLISHMENT_EVENT_VALUE', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_ALL_ESTABLISHMENT_EVENT_VALUE', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_ALL_ESTABLISHMENT_EVENT_VALUE', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');

alter table contract drop column rntrc;
alter table contractor drop column rntrc;