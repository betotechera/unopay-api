create table bank(
 bacen_cod integer PRIMARY KEY,
 name varchar(255) not null
);

create table bank_account(
 id VARCHAR(256) PRIMARY KEY,
 bacen_cod integer not null,
 agency varchar(20) not null,
 dv_agency varchar(10) not null,
 account_number varchar(50) not null,
 dv_account_number varchar(10) not null,
 account_type varchar(50) not null,
 constraint fk_bank_account foreign key(bacen_cod) REFERENCES bank(bacen_cod)

);

create table payment_bank_account (
 id VARCHAR(256) PRIMARY KEY,
 bank_account_id VARCHAR(256) not null,
 transfer_authorized char(1) not null,
 deposit_period varchar(50) not null,
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


ALTER TABLE oauth_user_details ADD issuer_id VARCHAR(256);
ALTER TABLE oauth_user_details ADD CONSTRAINT fK_user_issuer FOREIGN KEY(issuer_id) REFERENCES issuer(id);