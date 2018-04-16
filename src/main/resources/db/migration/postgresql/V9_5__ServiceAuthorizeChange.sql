alter table service_authorize add authorized_member_id varchar(256);
alter table service_authorize add constraint  fk_ser_auth_auth_mem foreign key(authorized_member_id) references authorized_member(id);
