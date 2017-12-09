insert into AUTHORITY(name, description) values('ROLE_LIST_BANK_ACCOUNT','Permite listar Contas Bancarias');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_BANK_ACCOUNT','Permite gerenciar Contas Bancarias');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_BANK_ACCOUNT', '1');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_BANK_ACCOUNT', '1');