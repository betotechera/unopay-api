create table PRODUCT_PAYMENT_INSTRUMENT_TP (
    product_id varchar(256) not null,
    payment_instrument_type varchar(256) not null,
    constraint fk_prod_prod_pit foreign key(product_id) references product(id)
);

create table product_credit_insertion_type (
    product_id varchar(256) not null,
    credit_insertion_type varchar(256) not null,
    constraint fk_prod_prod_cit foreign key(product_id) references product(id)
);

alter table product
    drop column credit_insertion_type;

alter table product
    drop column payment_instrument_type;