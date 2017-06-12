alter table contractor modify (rntrc null);
alter table service_authorize MODIFY authorization_number varchar(256);
alter table service_authorize MODIFY transaction_log_code int null;
alter table service_authorize MODIFY transaction_log varchar(256) null;
