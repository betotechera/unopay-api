alter table branch drop column invoice_mail;
alter table branch drop column alternative_mail;
alter table branch drop column cancellation_tolerance;
alter table branch drop column fee;
alter table branch drop column contract_uri;
alter table branch drop column movement_account_id;

alter table branch drop column movement_period;
alter table branch drop column authorize_transfer;
alter table branch drop column minimum_deposit_value;
alter table branch drop column closing_payment_days;

alter table branch drop column invoice_receipt_type;
alter table branch drop column invoice_receipt_period;

alter table address add micro_region varchar(100) null;
alter table bank_account add operation_type varchar(10) null;

create table establishment_branch_gathering(
    branch_id varchar(256) not null,
    gathering_channel varchar(256) not null,
    constraint fk_est_branch_gath foreign key(branch_id) references branch(id)
);

create table establishment_branch_service (
	branch_id varchar(256) not null,
	service_id varchar(256) not null,
	constraint fk_est_service_service foreign key(service_id) references service(id),
	constraint fk_est_branch_service foreign key(branch_id) references branch(id)
);




