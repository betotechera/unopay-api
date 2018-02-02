alter table boleto rename order_id to source_id;
alter table boleto rename client_document to payer_document;
alter table boleto add source_type varchar(100) not null default 'CONTRACTOR';
alter table boleto add occurrence_code varchar(100) null default '02';
alter table boleto alter processed_at drop not null;
alter table boleto rename to ticket;