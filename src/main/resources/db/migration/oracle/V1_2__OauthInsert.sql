-- pass secret
insert into oauth_client_details(client_id,client_secret,resource_ids,scope,authorized_grant_types,authorities,additional_information,access_token_validity,refresh_token_validity)
values ('unovation','c7bfc8f1ce2b3bcbdec9ae12dc4f04af0d9d1ad0253dcdcf8205b0a31bd4c6e5c4494095a762b79b','uaa','read,write','client_credentials,password,refresh_token,facebook,google','ROLE_CLIENT', '{"organization": "1"}',900,900);


insert into user_type(id, name, description, version) values('9574193f-603f-416f-bc48-09e2ddb3d3d6', 'EMISSORA', 'Instituição de Pagamento emissora', 0);
insert into user_type(id, name, description, version) values('abfe2082-c5c0-446e-9342-fe68b8acc3e4', 'CREDENCIADORA', 'Instituição de Pagamento credenciadora', 0);
insert into user_type(id, name, description, version) values('701ca5dc-a6ac-47df-986c-d543f49f0263', 'ESTABELECIMENTO', 'Estabelecimento', 0);
insert into user_type(id, name, description, version) values('0c6c0e54-3ea6-41a6-901c-d1ec0fca3d05', 'CONTRATANTE', 'Contratante', 0);
insert into user_type(id, name, description, version) values('865b362c-fc31-4a35-b118-6da78147559f', 'CONTRATADO', 'Contratado', 0);
insert into user_type(id, name, description, version) values('5ba95e66-5e88-4ba8-bdf4-66a7a480c252', 'PARCEIRO', 'Parceiro', 0);
insert into user_type(id, name, description, version) values('92af025e-1580-4c49-8246-f965e48c5721', 'PAYMENT_RULE_GROUP', 'Arranjo de pagamento', 0);

insert into oauth_user_details(id, email,name, type, password, version)
values('643f406a-0cfa-422c-bea2-e80dfc63af15', 'ti@unovation.com.br', 'unovation', '92af025e-1580-4c49-8246-f965e48c5721',  'a023dceb3b15e9b9586bc149e02b57d0f0b6929db9b84bfcaa607216d8fe638c4faf2ca543d2304a', 0);

insert into oauth_groups(id, group_name, description, version, user_type) values('99bf9ba6-75e4-4109-b5be-e4858f3f68b2','admin', 'Permite acesso completo ao sistema', 0, '92af025e-1580-4c49-8246-f965e48c5721');

insert into authority(name, description) values('ROLE_ADMIN', 'Permite acesso completo ao sistema');

insert into oauth_group_members(user_id, group_id) values('643f406a-0cfa-422c-bea2-e80dfc63af15', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');

insert into authority(name, description) values('ROLE_ADMIN', 'Permite acesso completo ao sistema');
insert into authority(name, description) values('ROLE_USER', 'Permite acesso ao sistema');
Insert into AUTHORITY(name, description) values('ROLE_LIST_ISSUERS','Permite listar Emissores');
Insert into AUTHORITY(name, description) values('ROLE_MANAGE_ISSUERS','Permite gerenciar Emissores');
Insert into AUTHORITY(name, description) values('ROLE_LIST_GROUPS','Permite listar Perfis de Acesso');
Insert into AUTHORITY(name, description) values('ROLE_MANAGE_GROUPS','Permite gerenciar Perfis de Acesso');
Insert into AUTHORITY(name, description) values('ROLE_LIST_USERS','Permite listar Usuarios');
Insert into AUTHORITY(name, description) values('ROLE_MANAGE_USERS','Permite gerenciar Usuarios');

insert into oauth_group_authorities(authority, group_id) values('ROLE_ADMIN', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_ISSUERS', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_ISSUERS', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_GROUPS', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_GROUPS', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_USERS', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_USERS', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');