create table batch_closing (
    id VARCHAR(256) PRIMARY KEY,
    establishment_id varchar(256) not null,
    issuer_id varchar(256) not null,
    accredited_network_id varchar(256) not null,
    closing_date_time TIMESTAMP not null,
    value decimal(20,2) not null,
    period varchar(256) not null,
    payment_release_date_time TIMESTAMP NOT NULL,
    situation varchar(50) not null,
    payment_date_time TIMESTAMP NOT NULL,
    payment_id varchar(256),
    version integer,
    constraint fk_batch_est foreign key(establishment_id) references establishment(id),
    constraint fk_batch_issuer foreign key(issuer_id) references issuer(id),
    constraint fk_batch_network foreign key(accredited_network_id) references accredited_network(id)
);

create table batch_closing_item (
    id VARCHAR(256) PRIMARY KEY,
    batch_closing_id varchar(256) not null,
    hirer_id varchar(256) not null,
    authorization_id varchar(256) not null,
    fiscal_document TIMESTAMP not null,
    invoice_number varchar(256),
    invoice_situation varchar(100) not null,
    invoice_document_uri varchar(256),
    version integer,
    constraint fk_batch_item foreign key(batch_closing_id) references batch_closing(id),
    constraint fk_batch_item_hirer foreign key(hirer_id) references hirer(id),
    constraint fk_batch_item_auth foreign key(authorization_id) references service_authorize(id)
);


insert into AUTHORITY(name, description) values('ROLE_LIST_BATCH_CLOSING','Permite listar fechamentos de lote.');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_BATCH_CLOSING','Permite gerenciar fechamentos de lote.');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_BATCH_CLOSING', '1');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_BATCH_CLOSING', '1');