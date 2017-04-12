create table branch_hirer (
    id VARCHAR(256) PRIMARY KEY,
    person_id VARCHAR(256),
    head_office_id VARCHAR(256),
    document_email varchar(256),
    bank_account_id varchar(256) not null,
    version integer,
    CONSTRAINT bhirer_person_id UNIQUE (person_id),
    constraint fk_bhirer_person foreign key(person_id) references person(id),
    constraint fk_branch_hirer foreign key(head_office_id) references hirer(id),
    constraint fk_bhirer_bank_account foreign key(bank_account_id) references bank_account(id)
);

insert into AUTHORITY(name, description) values('ROLE_LIST_BRANCH_HIRER','Permite listar Filial deContratante');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_BRANCH_HIRER','Permite gerenciar Filial de Contratante');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_BRANCH_HIRER', '1');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_BRANCH_HIRER', '1');