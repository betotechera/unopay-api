alter table credit rename column available_balance to available_value;
alter table credit rename column blocked_balance to blocked_value;
alter table payment_account drop constraint fk_h_cred_account;
alter table payment_account rename column payment_bank_account_id to payment_account_id;
alter table payment_account rename to credit_payment_account;