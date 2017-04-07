create table bank(
 bacen_code integer PRIMARY KEY,
 name varchar(255) not null
);

create table bank_account(
 id VARCHAR(256) PRIMARY KEY,
 bank_bacen_code integer not null,
 agency varchar(20) not null,
 agency_digit varchar(10),
 account_number varchar(50) not null,
 account_number_digit varchar(10),
 account_type varchar(50) not null,
 constraint fk_bank_account foreign key(bank_bacen_code) REFERENCES bank(bacen_code)

);

create table payment_bank_account (
 id VARCHAR(256) PRIMARY KEY,
 bank_account_id VARCHAR(256) not null,
 authorize_transfer char(1) not null,
 deposit_period varchar(50) not null,
 closing_payment_days integer,
 minimum_deposit_value  decimal(10,2),
 constraint fk_payment_bank_account foreign key(bank_account_id) REFERENCES bank_account(id)
);


create table issuer (
    id VARCHAR(256) PRIMARY KEY,
    person_id VARCHAR(256),
    tax decimal(3,2),
    payment_account_id varchar(256) not null,
    movement_account_id varchar(256) not null,
    version integer,
    CONSTRAINT issuer_pers_id UNIQUE (person_id),
    constraint fk_issuer_pers foreign key(person_id) references person(id),
    constraint fk_payment_account foreign key(payment_account_id) references payment_bank_account(id),
    constraint fk_movement_account foreign key(movement_account_id) references bank_account(id)
);

create table payment_rule_group_issuer (
	issuer_id varchar(256) not null,
	payment_rule_group_id varchar(256) not null,
	constraint fk_payment_rule_group_issuer foreign key(payment_rule_group_id) references payment_rule_group(id)
);


insert into AUTHORITY(name, description) values('ROLE_LIST_ISSUER','Permite listar Emissores');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_ISSUER','Permite gerenciar Emissores');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_ISSUER', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_ISSUER', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');

ALTER TABLE oauth_user_details ADD issuer_id VARCHAR(256);
ALTER TABLE oauth_user_details ADD CONSTRAINT fK_user_issuer FOREIGN KEY(issuer_id) REFERENCES issuer(id);
