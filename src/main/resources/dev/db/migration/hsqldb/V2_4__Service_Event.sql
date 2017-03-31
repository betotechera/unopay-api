create table service (
 id VARCHAR(256) PRIMARY KEY,
 type varchar(50) not null,
 name varchar(256) not null,
 tax_val decimal(5,2) not null,
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
