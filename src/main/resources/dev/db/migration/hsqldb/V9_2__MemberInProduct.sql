alter table product add member_annuity decimal(20,2) default 0.0 not null;
alter table contract add member_annuity decimal(20,2) default 0.0 not null;
alter table contract add member_total integer default 0 not null;
