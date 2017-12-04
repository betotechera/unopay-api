drop table contract_establishment;

create table contract_establishment (
    id varchar(256) PRIMARY KEY,
    contract_id varchar(256) not null,
    establishment_id varchar(256) not null,
    origin varchar(20),
    creation_date DATE,
    version integer,
    constraint fk_ce_contract foreign key(contract_id) references contract(id),
    constraint fk_ce_establishment foreign key(establishment_id) references establishment(id)
);

