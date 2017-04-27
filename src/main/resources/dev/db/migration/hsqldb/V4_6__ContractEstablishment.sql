create table contract_establishment (
    contract_id varchar(256) not null,
    establishment_id varchar(256) not null,
    constraint fk_ce_contract foreign key(contract_id) references contract(id),
    constraint fk_ce_establishment foreign key(establishment_id) references establishment(id)
);

