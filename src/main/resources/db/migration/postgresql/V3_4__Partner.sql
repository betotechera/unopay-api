create table partner (
    id VARCHAR(256) PRIMARY KEY,
    person_id VARCHAR(256),
    bank_account_id varchar(256),
    version integer,
    CONSTRAINT partner_person_id UNIQUE (person_id),
    constraint fk_partner_person foreign key(person_id) references person(id),
    constraint fk_partner_bank_account foreign key(bank_account_id) references bank_account(id)
);

insert into AUTHORITY(name, description) values('ROLE_LIST_PARTNER','Permite listar Contratado');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_PARTNER','Permite gerenciar Contratado');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_PARTNER', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_PARTNER', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');

ALTER TABLE oauth_user_details ADD partner_id VARCHAR(256);
ALTER TABLE oauth_user_details ADD CONSTRAINT fk_user_partner FOREIGN KEY(partner_id) REFERENCES partner(id);
