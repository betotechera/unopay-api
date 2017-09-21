create table contract_credit_insertion_type (
    contract_id varchar(256) not null,
    credit_insertion_type varchar(256) not null,
    constraint fk_con_con_cit foreign key(contract_id) references contract(id)
);

alter table contract
    drop column credit_insertion_type;

