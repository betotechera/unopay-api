alter table issuer add column payzen_shop_id varchar(50) default 'id loja' not null;
alter table issuer add column payzen_shop_key varchar(50) default 'key loja' not null;

alter table issuer add column wingoo_client_id varchar(50);
alter table issuer add column wingoo_client_secret varchar(256);

alter table transaction add column issuer_document varchar(256) default 'unovation' not null;
