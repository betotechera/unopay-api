create table partner_product (
    product_id varchar(256) not null,
    partner_id varchar(256) not null,
    constraint fk_product_partner foreign key(product_id) references product(id),
    constraint fk_partner_product foreign key(partner_id) references partner(id)
);
