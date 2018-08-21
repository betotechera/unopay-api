alter table "order" add create_user boolean default true;
alter table "order" add payment_method varchar(150) default 'BOLETO';