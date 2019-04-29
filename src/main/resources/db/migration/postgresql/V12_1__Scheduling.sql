CREATE TABLE scheduling (

    id VARCHAR(256) PRIMARY KEY,
    token VARCHAR(256) NOT NULL,
    created_date_time TIMESTAMP NOT NULL,
    branch_id VARCHAR(256) NOT NULL,
    contract_id VARCHAR(256) NOT NULL,
    contractor_id VARCHAR(256) NOT NULL,
    payment_instrument_id VARCHAR(256) NOT NULL,
    user_id VARCHAR(256) NOT NULL,
    service_description varchar(150) not null,
    service_type varchar(150) not null,
    authorized_member_id VARCHAR(256),
    scheduling_date TIMESTAMP NOT NULL,
    expiration_date TIMESTAMP,
    cancellation_date TIMESTAMP,

    constraint fk_branch_scheduling foreign key(branch_id) references branch(id),
    constraint fk_contract_scheduling foreign key(contract_id) references contract(id),
    constraint fk_contractor_scheduling foreign key(contractor_id) references contractor(id),
    constraint fk_payment_instru_scheduling foreign key(payment_instrument_id) references payment_instrument(id),
    constraint fk_user_scheduling foreign key(user_id) references oauth_user_details(id),
    constraint fk_authorized_member_scheduling foreign key(authorized_member_id) references authorized_member(id)

);

create table scheduling_event (
    scheduling_id varchar(256) not null,
    event_id varchar(256) not null,
    constraint fk_sch_event foreign key(scheduling_id) references scheduling(id),
    constraint fk_sch_sch foreign key(event_id) references event(id)
);

alter table establishment_event add column private_value decimal(20,2) default 0.0 not null;


insert into AUTHORITY(name, description) values('ROLE_LIST_SCHEDULING','Permite listar agendamentos');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_SCHEDULING','Permite gerenciar agendamentos');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_SCHEDULING', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_SCHEDULING', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
