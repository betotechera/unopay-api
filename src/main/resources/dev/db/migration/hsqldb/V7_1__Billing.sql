create table credit_order (
    id VARCHAR(256) PRIMARY KEY,
    number varchar(100) not null,
    product_id varchar(256) not null,
    person_id VARCHAR(256) not null,
    email varchar(256) not null,
    bank_account_id varchar(256) not null,
    create_date_time TIMESTAMP not null,
    version integer,
    constraint fk_order_person foreign key(person_id) references person(id),
    constraint fk_order_product foreign key(product_id) references product(id),
    constraint fk_order_bank_account foreign key(bank_account_id) references bank_account(id)
);

create table transaction (
    id VARCHAR(256) PRIMARY KEY,
    order_id VARCHAR(256) not null,
    payment_method varchar(100) not null,
    status varchar(100) not null,
    currency varchar(2) not null,
    value decimal(20,2) not null,
    installments integer not null,
    create_date_time Timestamp not null,
    version integer,
    constraint fk_transaction_order foreign key(order_id) references credit_order(id)
);