alter table product add member_annuity decimal(20,2) default 0.0 not null;
alter table contract add member_annuity decimal(20,2) default 0.0 not null;
alter table contract add member_total integer default 0 not null;

create table authorized_member_candidate(
    id VARCHAR(256) PRIMARY KEY,
    birth_date TIMESTAMP not null,
    name varchar(256) not null,
    gender varchar(50) not null,
    relatedness varchar(50) not null,
    email VARCHAR(256),
    document_type VARCHAR(50),
    document_number VARCHAR(50),
    registry_entity VARCHAR(50),
    order_id varchar(256),
    version INTEGER,
    constraint fk_auth_member_cand_order foreign key(order_id) references "order"(id)
);