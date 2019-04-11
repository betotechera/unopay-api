create table branch_service_period(
    id VARCHAR(256) PRIMARY KEY,
    branch_id VARCHAR(256) not null,
    weekday VARCHAR(256) not null,
    begin_service_time TIME not null,
    end_service_time TIME not null,
    situation VARCHAR(150) not null,
    created_date_time TIMESTAMP not null,
    version INTEGER,
    constraint fk_branc_srv_period foreign key(branch_id) references branch(id)
);

alter table branch add situation VARCHAR(150) default 'REGISTERED' not null





