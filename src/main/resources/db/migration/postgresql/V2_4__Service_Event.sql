create table service (
 id VARCHAR(256) PRIMARY KEY,
 type varchar(50) not null,
 code integer not null,
 name varchar(256) not null,
 tax_val decimal(20,2) not null,
 tax_percent decimal(3,2) not null
);

create table event (
 id VARCHAR(256) PRIMARY KEY,
 service_id varchar(256) not null,
 ncm_code varchar(50) not null,
 name varchar(256) not null,
 request_quantity char(1) not null,
 quantity_unity varchar(256),
 constraint fk_service_event foreign key(service_id) REFERENCES service(id)
);


insert into AUTHORITY(name, description) values('ROLE_LIST_EVENT','Permite listar Eventos');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_EVENT','Permite gerenciar Eventos');


insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_EVENT', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_EVENT', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');

insert into AUTHORITY(name, description) values('ROLE_LIST_SERVICE','Permite listar Serviços');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_SERVICE','Permite gerenciar Serviços');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_SERVICE', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_SERVICE', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');