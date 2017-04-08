create table subsidiary (
    id VARCHAR(256) PRIMARY KEY,
    person_id VARCHAR(256),
    matrix_id varchar(256),
    type varchar(100) not null,
    contact_mail varchar(256) not null,
    invoice_mail varchar(256) not null,
    alternative_mail varchar(256),
    tax decimal(3,2),
    cancellation_tolerance integer,
    technical_contact varchar(256),
    subsidiary_photo_uri varchar(256),
    contract_uri varchar(256),
    gathering_channel varchar(256),
    movement_account_id varchar(256) not null,
    movement_period VARCHAR(16),
    authorize_transfer varchar(1),
    closing_payment_days integer,
    minimum_deposit_value decimal(10,2),
    invoice_receipt_period  VARCHAR(16),
    invoice_receipt_type varchar(10),
    version integer,
    CONSTRAINT subsidiary_pers_id UNIQUE (person_id),
    constraint fk_subsidiary_person foreign key(person_id) references person(id),
    constraint fk_subsidiary_matrix foreign key(matrix_id) references establishment(id),
    constraint fk_subsidiary_movement_account foreign key(movement_account_id) references bank_account(id),
);

create table subsidiary_service (
	subsidiary_id varchar(256) not null,
	service_id varchar(256) not null,
	constraint fk_subsidiary_service_service foreign key(service_id) references service(id),
	constraint fk_subsidiary_service_subsidiary foreign key(subsidiary_id) references subsidiary(id)
);

insert into AUTHORITY(name, description) values('ROLE_LIST_SUBSIDIARY','Permite listar Filiais');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_SUBSIDIARY','Permite gerenciar Filiais');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_SUBSIDIARY', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_SUBSIDIARY', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');