-- pass secret
insert into oauth_client_details(client_id,client_secret,resource_ids,scope,authorized_grant_types,authorities,additional_information,access_token_validity,refresh_token_validity)
values ('client','a2ca3855f39f2fa6c0d553927ded0fc95b88e9672c173da32902d4cdb17f30eedcda8046e9ebca39','uaa','read,write','client_credentials,password,refresh_token,facebook,google','ROLE_CLIENT', '{"organization": "1"}',900,900);

-- pass refresh
insert into oauth_client_details(client_id,client_secret,resource_ids,scope,authorized_grant_types,authorities,additional_information,access_token_validity,refresh_token_validity)
values ('refresh','11a4faaed8ffc9038a0069fb83d9f7519953fa2e36772af5b4c19b3e9e9bbb860a4690c32d7ef2aa','uaa','read,write','client_credentials,password,refresh_token,facebook,google','ROLE_CLIENT', '{"organization": "1"}',2,60);

-- pass secret
insert into oauth_client_details(client_id,client_secret,resource_ids,scope,authorized_grant_types,authorities,additional_information,access_token_validity,refresh_token_validity)
values ('manager','a2ca3855f39f2fa6c0d553927ded0fc95b88e9672c173da32902d4cdb17f30eedcda8046e9ebca39','uaa','read,write','client_credentials,password,refresh_token,facebook,google','ROLE_CLIENT,ROLE_MANAGE_UAA_USERS','{"organization": "1"}',900,900);

-- pass test
insert into oauth_user_details(id, email, password, shop_id)
values('1', 'test@test.com', '89b90f0d09a812f5a30bc1c2f9e4d165baf453ce0a2f453092066a63e064890a035aff1a4fb60557', '1');

insert into oauth_user_details(id, email, password, shop_id)
values('2', 'salesman@unovation.com.br', '25449269c801e35004f0eb163ee00df067ff6e4f95926c686dcab687ca4ae1f3c39cad8627ab19d9', '1');

insert into oauth_user_details(id, email, password, shop_id)
values('3', 'withouanyrole@unovation.com.br', '25449269c801e35004f0eb163ee00df067ff6e4f95926c686dcab687ca4ae1f3c39cad8627ab19d9', '1');

insert into oauth_user_authorities(user_id, authority) values('1', 'ROLE_USER');

insert into authority(name, description) values('ROLE_ADMIN', 'Permite acesso completo ao sistema');