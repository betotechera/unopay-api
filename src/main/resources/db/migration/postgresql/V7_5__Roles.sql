insert into AUTHORITY(name, description) values('ROLE_LIST_USER_DETAIL','Permite listar usuarios');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_USER_DETAIL','Permite gerenciar usuarios');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_USER_DETAIL', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_USER_DETAIL', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');

insert into AUTHORITY(name, description) values('ROLE_LIST_USER_TYPE','Permite listar tipos de usuarios');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_USER_TYPE', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');

insert into AUTHORITY(name, description) values('ROLE_LIST_GROUPS','Permite listar grupos');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_GROUPS','Permite gerenciar grupos');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_GROUPS', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_GROUPS', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');

insert into AUTHORITY(name, description) values('ROLE_LIST_BANK_ACCOUNT','Permite listar Contas Bancarias');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_BANK_ACCOUNT','Permite gerenciar Contas Bancarias');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_BANK_ACCOUNT', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_BANK_ACCOUNT', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');

insert into AUTHORITY(name, description) values('ROLE_LIST_BRAND_FLAG','Permite listar Bancos');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_BRAND_FLAG', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');

insert into AUTHORITY(name, description) values('ROLE_LIST_ADDRESS','Permite listar Endereços');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_ADDRESS', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');

insert into AUTHORITY(name, description) values('ROLE_LIST_PERSON','Permite listar Pessoas');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_PERSON', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');