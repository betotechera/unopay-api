create table brand_flag (
    id VARCHAR(256) PRIMARY KEY,
    name varchar(100) not null,
    description varchar(256)
);

create table contact (
    id VARCHAR(256) PRIMARY KEY,
    name varchar(100) not null,
    mail varchar(256) not null,
    cell_phone varchar(256) not null,
    phone varchar(256)
);

create table establishment (
    id VARCHAR(256) PRIMARY KEY,
    person_id VARCHAR(256),
    type varchar(100) not null,
    contact_mail varchar(256) not null,
    invoice_mail varchar(256) not null,
    bach_shipment_mail varchar(256) not null,
    alternative_mail varchar(256),
    cancellation_tolerance integer,
    tax decimal(3,2),
    accredited_network_id varchar(256) not null,
    brand_flag_id varchar(256) not null,
    logo_uri varchar(256),
    operational_contact_id varchar(256),
    administrative_contact_id varchar(256),
    financier_contact_id varchar(256) not null,
    technical_contact varchar(256),
    establishment_photo_uri varchar(256),
    contract_uri varchar(256),
    gathering_channel varchar(256),
    movement_account_id varchar(256) not null,
    movement_period VARCHAR(16),
    authorize_transfer varchar(20),
    closing_payment_days integer,
    minimum_deposit_value decimal(10,2),
    invoice_receipt_period  VARCHAR(16),
    invoice_receipt_type varchar(10),
    version integer,
    CONSTRAINT est_pers_id UNIQUE (person_id),
    constraint fk_est_person foreign key(person_id) references person(id),
    constraint fk_est_accredited_network foreign key(accredited_network_id) references accredited_network(id),
    constraint fk_est_movement_account foreign key(movement_account_id) references bank_account(id),
    constraint fk_est_brand_flag foreign key(brand_flag_id) references brand_flag(id),
    constraint fk_est_operational_contact foreign key(operational_contact_id) references contact(id),
    constraint fk_est_administrative_contact foreign key(administrative_contact_id) references contact(id),
    constraint fk_est_financier_contact foreign key(financier_contact_id) references contact(id)
);

create table establishment_service (
	establishment_id varchar(256) not null,
	service_id varchar(256) not null,
	constraint fk_est_service_service foreign key(service_id) references service(id),
	constraint fk_est_service_est foreign key(establishment_id) references establishment(id)
);

insert into AUTHORITY(name, description) values('ROLE_LIST_ESTABLISHMENT','Permite listar Estabelecimentos');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_ESTABLISHMENT','Permite gerenciar Estabelecimentos');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_ESTABLISHMENT', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_ESTABLISHMENT', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');

ALTER TABLE oauth_user_details ADD establishment_id VARCHAR(256);
ALTER TABLE oauth_user_details ADD CONSTRAINT fK_user_establishment FOREIGN KEY(establishment_id) REFERENCES establishment(id);
