create table credit_order (
    id VARCHAR(256) PRIMARY KEY,
    order_number varchar(100) not null,
    product_id varchar(256) not null,
    person_id VARCHAR(256) not null,
    email varchar(256) not null,
    create_date_time TIMESTAMP not null,
    status varchar(150) not null,
    partner_id varchar(256),
    version integer,
    constraint fk_order_person foreign key(person_id) references person(id),
    constraint fk_order_product foreign key(product_id) references product(id)
);

create table transaction (
    id VARCHAR(256) PRIMARY KEY,
    order_id VARCHAR(256) not null,
    payment_method varchar(100) not null,
    status varchar(100) not null,
    currency varchar(5) not null,
    value decimal(*,2) not null,
    installments integer not null,
    create_date_time Timestamp not null,
    captured_requested_at Timestamp,
    cancellation_requested_at Timestamp,
    version integer
);