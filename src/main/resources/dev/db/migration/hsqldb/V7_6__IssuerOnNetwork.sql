create table accredited_network_issuer(
    id VARCHAR(256) PRIMARY KEY,
    ACCREDITED_NETWORK_ID varchar(256) not null,
    ISSUER_ID varchar(256) not null,
    CREATED_DATE_TIME timestamp not null,
    USER_ID varchar(256) not null,
    active integer not null,
    version integer,
    constraint fk_netiss_network foreign key(ACCREDITED_NETWORK_ID) references accredited_network(id),
    constraint fk_netiss_issuer foreign key(ISSUER_ID) references issuer(id)
);