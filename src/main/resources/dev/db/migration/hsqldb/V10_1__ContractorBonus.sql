create table contractor_bonus(
    id VARCHAR(256) PRIMARY KEY,
    product_id VARCHAR(256) not null,
    person_id VARCHAR(256) not null,
    contractor_id VARCHAR(256) not null,
    earned_bonus decimal(20,2) not null,
    situation VARCHAR(150) not null,
    processed_at TIMESTAMP,
    created_date_time TIMESTAMP not null,
    version INTEGER,

    constraint fk_auth_member_product foreign key(product_id) references product(id),
    constraint fk_auth_member_person foreign key(person_id) references person(id),
    constraint fk_auth_member_contractor foreign key(contractor_id) references contractor(id)
);

insert into AUTHORITY(name, description) values('ROLE_LIST_CONTRACTOR_BONUS','Permite listar bônus dos favorecidos');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_CONTRACTOR_BONUS','Permite gerenciar bônus dos favorecidos');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_CONTRACTOR_BONUS', '1');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_CONTRACTOR_BONUS', '1');