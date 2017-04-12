create table hired (
    id VARCHAR(256) PRIMARY KEY,
    person_id VARCHAR(256),
    rntrc varchar(256) not null,
    bank_account_id varchar(256),
    version integer,
    CONSTRAINT hired_person_id UNIQUE (person_id),
    constraint fk_hired_person foreign key(person_id) references person(id),
    constraint fk_hired_bank_account foreign key(bank_account_id) references bank_account(id)
);

insert into AUTHORITY(name, description) values('ROLE_LIST_HIRED','Permite listar Contratado');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_HIRED','Permite gerenciar Contratado');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_HIRED', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_HIRED', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');

ALTER TABLE oauth_user_details ADD hired_id VARCHAR(256);
ALTER TABLE oauth_user_details ADD CONSTRAINT fk_user_hired FOREIGN KEY(hired_id) REFERENCES hired(id);

alter table person add cell_phone varchar(256);
