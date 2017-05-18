create table service_authorize (
    id VARCHAR(256) PRIMARY KEY,
    authorization_number bigint not null,
    authorization_date_time TIMESTAMP not null,
    establishment_id varchar(256) not null,
    contract_id varchar(256) not null,
    contractor_id varchar(256) not null,
    service_type varchar(100) not null,
    event_id varchar(256) not null,
    event_quantity integer,
    event_value decimal(20,2),
    value_fee decimal(20,2) not null,
    solicitation_date_time TIMESTAMP not null,
    credit_insertion_type varchar(100) not null,
    contractor_instrument_credit_id varchar(256),
    last_instrument_credit_balance decimal(20,2) not null,
    current_instrument_credit_balance decimal(20,2) not null,
    cancellation_date_time TIMESTAMP,
    transaction_log_code int not null,
    transaction_log varchar(256) not null,
    user_id varchar(256) not null,
    situation varchar(50) not null,
    version integer,
    constraint fk_serv_auth_est foreign key(establishment_id) references establishment(id),
    constraint fk_serv_auth_contract foreign key(contract_id) references contract(id),
    constraint fk_serv_auth_contractor foreign key(contractor_id) references contractor(id),
    constraint fk_serv_auth_event foreign key(event_id) references event(id),
    constraint fk_serv_auth_instr_cred foreign key(contractor_instrument_credit_id) references contractor_instrument_credit(id),
    constraint fk_serv_auth_user foreign key(user_id) references oauth_user_details(id)
);

insert into AUTHORITY(name, description) values('ROLE_LIST_SERVICE_AUTHORIZE','Permite listar autorizaçoes de serviços.');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_SERVICE_AUTHORIZE','Permite gerenciar autorizaçoes de serviços.');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_SERVICE_AUTHORIZE', '1');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_SERVICE_AUTHORIZE', '1');
