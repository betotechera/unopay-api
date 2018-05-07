create table bonus_billing (
    id VARCHAR(256) PRIMARY KEY,
    contract_id varchar(256) not null,
    created_date_time timestamp,
    person_id varchar(256) not null,
    total decimal(20,2) not null,
    processed_at timestamp,
    number varchar(100) not null,
    expiration timestamp not null,
    status varchar(100) not null
    constraint fk_bonus_bill_person foreign key(contract_id) references person(id)
);