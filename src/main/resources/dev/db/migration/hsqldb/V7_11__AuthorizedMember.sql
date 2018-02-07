create table authorized_member(
    id VARCHAR(256) PRIMARY KEY,
    birth_date TIMESTAMP not null,
    name varchar(255) not null,
    gender varchar(50) not null,
    relatedness varchar(50) not null,
    email VARCHAR(256),
    document_type VARCHAR(50),
    document_number VARCHAR(50),
    registry_entity VARCHAR(50),
    payment_instrument_id VARCHAR(256),
    constraint fk_auth_member_pay_inst foreign key(payment_instrument_id) references payment_instrument(id)
);