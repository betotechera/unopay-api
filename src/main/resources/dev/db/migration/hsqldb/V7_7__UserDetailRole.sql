insert into AUTHORITY(name, description) values('ROLE_LIST_USER_DETAIL','Permite listar usuarios');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_USER_DETAIL','Permite gerenciar usuarios');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_USER_DETAIL', '1');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_USER_DETAIL', '1');