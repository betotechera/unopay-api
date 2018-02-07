create table hirer_negotiation (
    id VARCHAR(256) PRIMARY KEY,
    hirer_id VARCHAR(256) not null,
    product_id varchar(256) not null,
    default_credit_value decimal(20,2) not null,
    default_member_credit_value decimal(20,2) not null,
    payment_day integer not null,
    installments integer not null,
    installment_value decimal(20,2) not null,
    installment_value_by_member decimal(20,2) not null,
    credit_recurrence_period varchar(100) not null,
    free_installment_quantity integer not null,
    auto_renewal varchar(20) not null,
    effective_date timestamp not null,
    created_date_time Timestamp not null,
    "active" varchar(20) not null,
    version integer,
    constraint fk_hirer_neg_hirer foreign key(hirer_id) references hirer(id),
    constraint fk_hirer_neg_product foreign key(product_id) references product(id)
);

insert into AUTHORITY(name, description) values('ROLE_LIST_HIRER_CONTRACTS_DEFINATION','Permite listar definiçao de contratos do contratante.');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_HIRER_CONTRACTS_DEFINATION','Permite gerenciar definiçao de contratos do contratante');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_HIRER_CONTRACTS_DEFINATION', '1');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_HIRER_CONTRACTS_DEFINATION', '1');
