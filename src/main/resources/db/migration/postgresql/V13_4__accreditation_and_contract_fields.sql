alter table establishment add column returning_deadline integer default 0 not null;
alter table contract add column recurrence_payment_method varchar(256) default 'BOLETO' not null;
alter table "order" add column recurrence_payment_method varchar(256);