create table credit (
    id VARCHAR(256) PRIMARY KEY,
    product_id varchar(256),
    payment_rule_group_id varchar(256) not null,
    hirer_document varchar(20) not null,
    service_type varchar(100),
    credit_insertion_type varchar(100) not null,
    credit_number bigint not null,
    created_date_time TIMESTAMP not null,
    value decimal(20,2) not null,
    situation varchar(50) not null,
    version integer,
    constraint fk_credit_product foreign key(product_id) references product(id),
    constraint fk_credit_pay_rule_group foreign key(payment_rule_group_id) references payment_rule_group(id)
);

insert into AUTHORITY(name, description) values('ROLE_LIST_CREDIT','Permite listar Creditos');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_CREDIT','Permite gerenciar Creditos');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_CREDIT', '1');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_CREDIT', '1');