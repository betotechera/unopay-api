create table hirer (
    id VARCHAR(256) PRIMARY KEY,
    person_id VARCHAR(256),
    document_email varchar(256),
    bank_account_id varchar(256) not null,
    version integer,
    CONSTRAINT hirer_person_id UNIQUE (person_id),
    constraint fk_hirer_person foreign key(person_id) references person(id),
    constraint fk_hirer_bank_account foreign key(bank_account_id) references bank_account(id)
);

insert into AUTHORITY(name, description) values('ROLE_LIST_HIRER','Permite listar Contratante');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_HIRER','Permite gerenciar Contratante');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_HIRER', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_HIRER', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');

ALTER TABLE oauth_user_details ADD hirer_id VARCHAR(256);
ALTER TABLE oauth_user_details ADD CONSTRAINT fk_user_hirer FOREIGN KEY(hirer_id) REFERENCES hirer(id);

ALTER TABLE physical_person_detail ADD email VARCHAR(256);
