alter table credit add column issuer_id varchar(256) not null;
alter table credit add column hirer_id varchar(256) not null;
alter table credit drop column issuer_document;
alter table credit drop column hirer_document;
alter table hirer add column financier_mail varchar(100) null;