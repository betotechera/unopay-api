alter table branch add column returning_deadline integer default 0 not null;

alter table branch_service_period add column begin_lunch_time TIME DEFAULT CURRENT_TIME not null;
alter table branch_service_period add column end_lunch_time TIME DEFAULT CURRENT_TIME not null;
 