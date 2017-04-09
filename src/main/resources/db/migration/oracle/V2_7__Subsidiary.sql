create table subsidiary (
    id VARCHAR(256) PRIMARY KEY,
    person_id VARCHAR(256),
    matrix_id varchar(256),
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
    authorize_transfer char(1),
    closing_payment_days integer,
    minimum_deposit_value decimal(10,2),
    invoice_receipt_period  VARCHAR(16),
    invoice_receipt_type varchar(10),
    version integer,
    CONSTRAINT sub_pers_id UNIQUE (person_id),
    constraint fk_sub_person foreign key(person_id) references person(id),
    constraint fk_sub_matrix foreign key(matrix_id) references establishment(id),
    constraint fk_sub_movement_account foreign key(movement_account_id) references bank_account(id)
);

insert into AUTHORITY(name, description) values('ROLE_LIST_SUBSIDIARY','Permite listar Filiais');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_SUBSIDIARY','Permite gerenciar Filiais');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_SUBSIDIARY', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_SUBSIDIARY', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');