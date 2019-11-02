create table hirer_product (
    id VARCHAR(256) PRIMARY KEY,
    created_date_time timestamp,
    hirer_id varchar(256) not null,
    product_id varchar(256) not null,
    expiration timestamp not null,
    constraint fk_hirer_product_hirer foreign key(hirer_id) references hirer(id),
    constraint fk_hirer_product_product foreign key(product_id) references product(id)
);

insert into AUTHORITY(name, description) values('ROLE_LIST_HIRER_PRODUCT','Permite listar produtos do contratante');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_HIRER_PRODUCT','Permite gerenciar produtos do contratante');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_HIRER_PRODUCT', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_HIRER_PRODUCT', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
 