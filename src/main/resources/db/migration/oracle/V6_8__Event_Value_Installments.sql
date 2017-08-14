alter table person add short_name varchar(50) default '' not null;
alter table product add contract_validity_days integer default 99999 not null;
alter table product add annuity decimal(*,2) default 100.50 not null;
alter table product add payment_installments integer default 10 not null;
alter table establishment drop column brand_flag_id;
alter table establishment add facade_photo_uri varchar(256) null;
alter table event add request_value char(1) default '1' not null;
alter table payment_instrument add gateway_token varchar(256) null;
alter table physical_person_detail add gender varchar(10) default 'MAN' not null;
alter table contract add annuity decimal(*,2) default 100.50 not null;
alter table contract add membership_fee decimal(*,2) default 20.20 not null;
alter table contract add payment_installments integer default 10 not null;

create table establishment_event (
    id VARCHAR(256) PRIMARY KEY,
    event_id varchar(256) not null,
    establishment_id varchar(256) not null,
    value decimal(*,2) not null,
    expiration Timestamp not null,
    version integer not null,
    constraint fk_event_value foreign key(event_id) references event(id)
);