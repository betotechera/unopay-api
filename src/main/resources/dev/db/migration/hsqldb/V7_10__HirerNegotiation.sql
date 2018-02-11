create table hirer_negotiation (
    id VARCHAR(256) PRIMARY KEY,
    hirer_id VARCHAR(256) not null,
    product_id varchar(256) not null,
    default_credit_value decimal(20,2) not null,
    default_member_credit_value decimal(20,2) not null,
    payment_day integer not null,
    installments integer not null,
    billing_with_credits varchar(20) not null,
    installment_value decimal(20,2) not null,
    installment_value_by_member decimal(20,2) not null,
    free_installment_quantity integer not null,
    auto_renewal varchar(20) not null,
    effective_date timestamp not null,
    created_date_time Timestamp not null,
    "active" varchar(20) not null,
    version integer,
    constraint fk_hirer_neg_hirer foreign key(hirer_id) references hirer(id),
    constraint fk_hirer_neg_product foreign key(product_id) references product(id)
);

create table negotiation_billing (
    id VARCHAR(256) PRIMARY KEY,
    hirer_negotiation_id VARCHAR(256) not null,
    "number" varchar(256) not null,
    installment_number integer not null,
    installment_expiration timestamp not null,
    installments integer not null,
    installment_value decimal(20,2) not null,
    installment_value_by_member decimal(20,2) not null,
    free_installment_quantity integer not null,
    default_credit_value decimal(20,2) not null,
    default_member_credit_value decimal(20,2) not null,
    created_date_time Timestamp not null,
    billing_with_credits varchar(20) not null,
    credit_id varchar(256),
    value decimal(20,2) not null,
    credit_value decimal(20,2),
    status varchar(150) not null,
    version integer,
    constraint fk_neg_billing_hirer foreign key(hirer_negotiation_id) references hirer_negotiation(id),
    constraint fk_neg_billing_credit foreign key(credit_id) references credit(id)
);

create table negotiation_billing_detail (
    id VARCHAR(256) PRIMARY KEY,
    negotiation_billing_id varchar(256) not null,
    contract_id varchar(256) not null,
    value decimal(20,2) not null,
    credit_value decimal(20,2) not null,
    member_credit_value decimal(20,2) not null,
    member_total integer not null,
    installment_value decimal(20,2) not null,
    installment_value_by_member decimal(20,2) not null,
    free_installment varchar(20) not null,
    created_date_time Timestamp not null,
    version integer,
    constraint fk_neg_billing_det_neg foreign key(negotiation_billing_id) references negotiation_billing(id),
    constraint fk_neg_billing_det_contract foreign key(contract_id) references contract(id)
);


insert into AUTHORITY(name, description) values('ROLE_LIST_HIRER_NEGOTIATION','Permite listar negociaçoes de contratos do contratante.');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_HIRER_NEGOTIATION','Permite gerenciar negociaçoes de contratos do contratante');

insert into AUTHORITY(name, description) values('ROLE_LIST_HIRER_NEGOATIATION_BILLING','Permite listar os pagamentos negociaçoes de contratos do contratante.');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_HIRER_NEGOATIATION_BILLING','Permite gerenciar os pagamentos negociaçoes de contratos do contratante.');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_HIRER_NEGOTIATION', '1');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_HIRER_NEGOTIATION', '1');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_HIRER_NEGOATIATION_BILLING', '1');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_HIRER_NEGOATIATION_BILLING', '1');