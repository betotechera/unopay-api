create table hirer_product (
    id VARCHAR(256) PRIMARY KEY,
    created_date_time timestamp,
    hirer_id varchar(256) not null,
    product_id varchar(256) not null,
    expiration timestamp not null,
    constraint fk_hirer_product_hirer foreign key(hirer_id) references hirer(id),
    constraint fk_hirer_product_product foreign key(product_id) references product(id)
);

alter table "order" add column recurrence_credit_card_token varchar(256);
alter table "order" add column recurrence_credit_card_month varchar(2);
alter table "order" add column recurrence_credit_card_year varchar(4);
alter table "order" add column recurrence_credit_card_last_four_digits varchar(4);
alter table "order" add column recurrence_credit_card_brand varchar(30);
alter table "order" add column recurrence_credit_card_holder_name varchar(55);

insert into AUTHORITY(name, description) values('ROLE_LIST_HIRER_PRODUCT','Permite listar produtos do contratante');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_HIRER_PRODUCT','Permite gerenciar produtos do contratante');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_HIRER_PRODUCT', '1');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_HIRER_PRODUCT', '1');
 