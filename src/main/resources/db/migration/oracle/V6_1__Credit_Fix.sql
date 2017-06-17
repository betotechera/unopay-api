alter table contractor_instrument_credit add credit_type varchar(100) default 'PAY_ADVANCE' not null;
alter table issuer rename column tax to fee;
alter table branch rename column tax to fee;
alter table establishment rename column tax to fee;
alter table service rename column tax_val to fee_val;
alter table service rename column tax_percent to fee_percent;
