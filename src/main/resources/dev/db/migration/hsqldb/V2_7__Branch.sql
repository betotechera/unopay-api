create table branch (
    id VARCHAR(256) PRIMARY KEY,
    person_id VARCHAR(256),
    head_office_id varchar(256),
    contact_mail varchar(256) not null,
    invoice_mail varchar(256) not null,
    alternative_mail varchar(256),
    tax decimal(3,2),
    cancellation_tolerance integer,
    technical_contact varchar(256),
    branch_photo_uri varchar(256),
    contract_uri varchar(256),
    gathering_channel varchar(256),
    movement_account_id varchar(256) not null,
    movement_period VARCHAR(16),
    authorize_transfer char(1),
    closing_payment_days integer,
    minimum_deposit_value decimal(10,2),
    invoice_receipt_period  VARCHAR(16),
    invoice_receipt_type varchar(10),
    version integer,
    CONSTRAINT branch_pers_id UNIQUE (person_id),
    constraint fk_branch_person foreign key(person_id) references person(id),
    constraint fk_branch_head_office foreign key(head_office_id) references establishment(id),
    constraint fk_branch_movement_account foreign key(movement_account_id) references bank_account(id),
);

create table branch_service (
	branch_id varchar(256) not null,
	service_id varchar(256) not null,
	constraint fk_branch_service_service foreign key(service_id) references service(id),
	constraint fk_branch_service_branch foreign key(branch_id) references branch(id)
);

insert into AUTHORITY(name, description) values('ROLE_LIST_BRANCH','Permite listar Filiais');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_BRANCH','Permite gerenciar Filiais');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_BRANCH', '1');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_BRANCH', '1');

ALTER TABLE PERSON MODIFY name varchar(150);
ALTER TABLE LEGAL_PERSON_DETAIL MODIFY fantasy_name varchar(150);