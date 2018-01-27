alter table credit add issuer_id varchar(256) not null;
alter table credit add hirer_id varchar(256) not null;
alter table credit drop issuer_document;
alter table credit drop hirer_document;
