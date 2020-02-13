alter table "order" add hirer_id varchar(256);
alter table "order" add constraint fk_order_hirer foreign key(hirer_id) references hirer(id);