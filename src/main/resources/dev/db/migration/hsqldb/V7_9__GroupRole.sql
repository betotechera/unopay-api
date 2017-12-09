insert into AUTHORITY(name, description) values('ROLE_LIST_GROUPS','Permite listar grupos');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_GROUPS','Permite gerenciar grupos');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_GROUPS', '1');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_GROUPS', '1');