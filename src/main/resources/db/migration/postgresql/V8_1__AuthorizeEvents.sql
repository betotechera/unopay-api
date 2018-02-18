create table service_authorize_event (
    id VARCHAR(256) PRIMARY KEY,
    establishment_event_id VARCHAR(256) not null,
    service_authorize_id VARCHAR(256),
    event_id varchar(256) not null,
    service_type varchar(150) not null,
    event_value decimal(20,2) not null,
    value_fee decimal(20,2) not null,
    event_quantity decimal(20,2),
    created_date_time TIMESTAMP not null,
    version INTEGER,
    constraint fk_serv_auth_events_event foreign key(establishment_event_id) references establishment_event(id),
    constraint fk_serv_auth_events_service foreign key(service_authorize_id) references service_authorize(id)
);

alter table service_authorize add value decimal(20,2) not null;
alter table service_authorize add partial_payment varchar(20);
alter table service_authorize add exceptional_circumstance varchar(20);

alter table service_authorize drop event_quantity;
alter table service_authorize drop value_fee;
alter table service_authorize drop credit_insertion_type;
alter table service_authorize drop solicitation_date_time;
alter table service_authorize drop event_id;
alter table service_authorize drop service_type;
alter table service_authorize drop event_value;


