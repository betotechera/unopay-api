
create table accredited_network (
    id VARCHAR(256) PRIMARY KEY,
    person_id VARCHAR(256),
    merchant_discount_rate decimal(3,2),
    type VARCHAR(10),
    movement_period VARCHAR(16),
    authorize_transfer boolean,
    closing_payment_days integer,
    minimum_deposit_value  decimal(3,2),
    invoice_receipt_period  VARCHAR(16),
    invoice_receipt_type varchar(10),
    bank_account_id varchar(256),
    version integer,
    CONSTRAINT an_pers_id UNIQUE (person_id),
    constraint fk_an_pers foreign key(person_id) references person(id),
    constraint fk_an_bank_account foreign key(bank_account_id) references bank_account(id)
);

create table accredited_payment_rules (
	accredited_network_id varchar(256) not null,
	payment_rule_group_id varchar(256) not null,
	constraint fk_payment_rule_group_an foreign key(payment_rule_group_id) references payment_rule_group(id),
	constraint fk_accredited_network_prg foreign key(accredited_network_id) references accredited_network(id)
);


insert into AUTHORITY(name, description) values('ROLE_LIST_ACCREDITED_NETWORK','Permite listar Rede Credenciada');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_ACCREDITED_NETWORK','Permite gerenciar Rede Credenciada');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_ACCREDITED_NETWORK', '1');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_ACCREDITED_NETWORK', '1');

ALTER TABLE oauth_user_details ADD accredited_network_id VARCHAR(256);
ALTER TABLE oauth_user_details ADD CONSTRAINT fK_user_accredited_network FOREIGN KEY(accredited_network_id) REFERENCES accredited_network(id);
