alter table boleto alter column order_id rename to source_id;
alter table boleto alter column client_document rename to payer_document;
alter table boleto add column source_type varchar(100) not null;
alter table boleto add column occurrence_code varchar(100) null;
alter table boleto ALTER COLUMN processed_at set null;