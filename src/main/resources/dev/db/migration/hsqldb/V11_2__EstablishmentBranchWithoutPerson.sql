alter table branch drop column person_id;

alter table branch add name varchar(50) default '' not null;
alter table branch add fantasy_name varchar(50) null;
alter table branch add short_name varchar(20) default '' not null;
alter table branch add address_id varchar(256) default '' not null;

ALTER TABLE branch ADD CONSTRAINT fk_branch_address FOREIGN KEY(address_id) REFERENCES address(id);





