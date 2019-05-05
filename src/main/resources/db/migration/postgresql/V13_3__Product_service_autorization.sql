alter table service add column scheduling_allowed boolean;
alter table service_authorize add column scheduling_id varchar(256);
alter table service_authorize add column scheduling_token varchar(100);

create table product_payment_methods (
    product_id varchar(256) not null,
    payment_method varchar(256) not null,
    constraint fk_prod_pay_methods foreign key(product_id) references product(id)
);