insert into AUTHORITY(name, description) values('ROLE_LIST_USER_TYPE','Permite listar tipos de usuarios');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_USER_TYPE', '1');
