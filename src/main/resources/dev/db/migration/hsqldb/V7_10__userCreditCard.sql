create table user_credit_card (
    id VARCHAR(256) PRIMARY KEY,
    user_id VARCHAR(256) not null,
    holder_name VARCHAR(256) not null,
    brand VARCHAR(128) not null,
    last_four_digits VARCHAR(4) not null,
    expiration_month VARCHAR(2) not null,
    expiration_year VARCHAR(4) not null,
    gateway_source VARCHAR(128) not null,
    gateway_token VARCHAR(256) not null,
    created_date_time TIMESTAMP not null,
    version INTEGER,
    constraint fk_credit_card_user foreign key(user_id) references oauth_user_details(id),
);

insert into AUTHORITY(name, description) values('ROLE_LIST_USER_CREDIT_CARD','Permite listar os cartoes de credito de usuarios.');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_USER_CREDIT_CARD','Permite gerenciar os cartoes de credito de usuarios');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_USER_CREDIT_CARD', '1');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_USER_CREDIT_CARD', '1');