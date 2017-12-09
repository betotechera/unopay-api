alter table contractor alter column rntrc drop not null;
alter table service_authorize alter column authorization_number type varchar(256);
alter table service_authorize alter column transaction_log_code type int;
alter table service_authorize alter column transaction_log_code drop not null;
alter table service_authorize alter column transaction_log type varchar(256);
alter table service_authorize alter column transaction_log drop not null;
