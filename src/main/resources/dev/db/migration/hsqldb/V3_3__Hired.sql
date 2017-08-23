create table contractor (
    id VARCHAR(256) PRIMARY KEY,
    person_id VARCHAR(256),
    bank_account_id varchar(256),
    version integer,
    CONSTRAINT contractor_person_id UNIQUE (person_id),
    constraint fk_contractor_person foreign key(person_id) references person(id),
    constraint fk_contractor_bank_account foreign key(bank_account_id) references bank_account(id)
);

insert into AUTHORITY(name, description) values('ROLE_LIST_CONTRACTOR','Permite listar Contratado');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_CONTRACTOR','Permite gerenciar Contratado');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_CONTRACTOR', '1');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_CONTRACTOR', '1');

ALTER TABLE oauth_user_details ADD contractor_id VARCHAR(256);
ALTER TABLE oauth_user_details ADD CONSTRAINT fk_user_contractor FOREIGN KEY(contractor_id) REFERENCES contractor(id);

alter table person add cell_phone varchar(256);
