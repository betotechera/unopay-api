create table bonus_billing (
    id VARCHAR(256) PRIMARY KEY,
    created_date_time timestamp,
    person_id varchar(256) not null,
    issuer_id varchar(256) not null,
    total decimal(20,2) not null,
    processed_at timestamp,
    number varchar(100) not null,
    expiration timestamp not null,
    status varchar(100) not null,
    constraint fk_bonus_bill_person foreign key(person_id) references person(id),
    constraint fk_bonus_bill_issuer foreign key(issuer_id) references issuer(id)
);

create table contractor_bonus_billing (
    id VARCHAR(256) PRIMARY KEY,
    bonus_billing_id VARCHAR(256),
    contractor_bonus_id VARCHAR(256),
    constraint fk_cbb_bonus_billing foreign key(bonus_billing_id) references bonus_billing(id),
    constraint fk_cbb_contractor_bonus foreign key(contractor_bonus_id) references contractor_bonus(id)
);

insert into AUTHORITY(name, description) values('ROLE_LIST_BONUS_BILLING','Permite listar Faturamentos de bonus');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_BONUS_BILLING','Permite gerenciar Faturamentos de bonus');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_BONUS_BILLING', '1');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_BONUS_BILLING', '1');