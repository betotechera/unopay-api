create table service (
 id VARCHAR(256) PRIMARY KEY,
 type varchar(50) not null,
 name varchar(256) not null,
  code integer not null,
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

insert into service(id, code,type, name, tax_val, tax_percent) values ('1', 1,'ELECTRONIC_TOLL', 'Super servicço master', 2300.45, 0.3);
insert into service(id, code,type, name, tax_val, tax_percent) values ('2', 2,'FREIGHT', 'Serviço teste', 5, 0.1);
insert into event(id, service_id,ncm_code, name, request_quantity) values ('1', '2','1','Evento',0);
