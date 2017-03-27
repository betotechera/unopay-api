create table legal_person_detail (
  id VARCHAR(256) PRIMARY KEY,
  fantasy_name VARCHAR(50),
  responsible_name VARCHAR(50),
  responsible_email VARCHAR(50),
  responsible_document_type VARCHAR(50),
  responsible_document_number VARCHAR(50),
  responsible_registry_entity VARCHAR(50),
  version integer
);

create table physical_person_detail (
  id VARCHAR(256) PRIMARY KEY,
  version integer
);

create table address (
  id VARCHAR(256) PRIMARY KEY,
  zip_code VARCHAR(50),
  street_name VARCHAR(250),
  street_number VARCHAR(50),
  complement VARCHAR(50),
  district VARCHAR(50),
  city VARCHAR(50),
  state VARCHAR(50),
  latitude NUMBER(10,6),
  longitude NUMBER(10,6),
  version integer
);

create table person (
     id VARCHAR(256) PRIMARY KEY,
     name VARCHAR(50),
     type VARCHAR(50),
     document_type VARCHAR(50),
     document_number VARCHAR(50),
     registry_entity VARCHAR(50),
     telephone VARCHAR(50),
     address_id VARCHAR(256),
     physical_person_detail_id VARCHAR(256),
     legal_person_detail_id VARCHAR(256),
     version integer,
     CONSTRAINT person_document_number_uk UNIQUE (document_number,document_type),
    constraint fk_person_address foreign key(address_id) references address(id),
    constraint fk_person_detail foreign key(physical_person_detail_id) references physical_person_detail(id),
    constraint fk_legal_person_detail foreign key(legal_person_detail_id) references legal_person_detail(id)
);

create table instituition (
    id VARCHAR(256) PRIMARY KEY,
    person_id VARCHAR(256),
    payment_rule_group_id VARCHAR(256),
    version integer,
    CONSTRAINT inst_pers_id UNIQUE (person_id),
    constraint fk_inst_pers foreign key(person_id) references person(id),
    constraint fk_inst_prg foreign key(payment_rule_group_id) references payment_rule_group(id)
);

Insert into AUTHORITY(name, description) values('ROLE_LIST_INSTITUITION','Permite listar Instituidores');
Insert into AUTHORITY(name, description) values('ROLE_MANAGE_INSTITUITION','Permite gerenciar Instituidores');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_INSTITUITION', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_INSTITUITION', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');

ALTER TABLE oauth_user_details ADD instituition_id VARCHAR(256);
ALTER TABLE oauth_user_details ADD CONSTRAINT fK_user_inst FOREIGN KEY(instituition_id) REFERENCES instituition(id);
