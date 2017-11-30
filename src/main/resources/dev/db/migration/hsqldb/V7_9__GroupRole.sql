insert into AUTHORITY(name, description) values('ROLE_LIST_GROUP','Permite listar grupos');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_GROUP','Permite gerenciar grupos');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_GROUP', '1');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_GROUP', '1');