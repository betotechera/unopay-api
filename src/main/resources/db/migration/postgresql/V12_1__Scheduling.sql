CREATE TABLE scheduling (

    id VARCHAR(256) PRIMARY KEY,
    token VARCHAR(256) NOT NULL,
    created_date_time TIMESTAMP NOT NULL,
    branch_id VARCHAR(256) NOT NULL,
    contract_id VARCHAR(256) NOT NULL,
    contractor_id VARCHAR(256) NOT NULL,
    payment_instrument_id VARCHAR(256) NOT NULL,
    user_id VARCHAR(256) NOT NULL,
    authorized_member_id VARCHAR(256) NOT NULL,

    constraint fk_branch_scheduling foreign key(branch_id) references branch(id),
    constraint fk_contract_scheduling foreign key(contract_id) references contract(id),
    constraint fk_contractor_scheduling foreign key(contractor_id) references contractor(id),
    constraint fk_payment_instru_scheduling foreign key(payment_instrument_id) references payment_instrument(id),
    constraint fk_user_scheduling foreign key(user_id) references oauth_user_details(id),
    constraint fk_authorized_member_scheduling foreign key(authorized_member_id) references authorized_member(id)

);