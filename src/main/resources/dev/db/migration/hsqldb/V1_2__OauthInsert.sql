-- pass secret
insert into oauth_client_details(client_id,client_secret,resource_ids,scope,authorized_grant_types,authorities,additional_information,access_token_validity,refresh_token_validity)
values ('unovation','c7bfc8f1ce2b3bcbdec9ae12dc4f04af0d9d1ad0253dcdcf8205b0a31bd4c6e5c4494095a762b79b','uaa','read,write','client_credentials,password,refresh_token,facebook,google','ROLE_CLIENT', '{"organization": "1"}',900,900);

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
insert into oauth_user_details(id, email, password)
values('1', 'test@test.com', '15c6941ad4b520e2549dc5b88f28bd856baac09553872ed56cd60f48935d0e9a43aa7bad9b7e0206');

insert into oauth_user_details(id, email, password)
values('2', 'salesman@unovation.com.br', '25449269c801e35004f0eb163ee00df067ff6e4f95926c686dcab687ca4ae1f3c39cad8627ab19d9');

insert into oauth_user_details(id, email, password)
values('3', 'withouanyrole@unovation.com.br', '25449269c801e35004f0eb163ee00df067ff6e4f95926c686dcab687ca4ae1f3c39cad8627ab19d9');

insert into oauth_user_details(id, email, password)
values('5', 'ti@unovation.com.br', 'c7bfc8f1ce2b3bcbdec9ae12dc4f04af0d9d1ad0253dcdcf8205b0a31bd4c6e5c4494095a762b79b');

insert into authority(name, description) values('ROLE_ADMIN', 'Permite acesso completo ao sistema');
insert into authority(name, description) values('ROLE_USER', 'Permite acesso ao sistema');

insert into oauth_user_authorities(user_id, authority) values('1', 'ROLE_USER');
insert into oauth_user_authorities(user_id, authority) values('1', 'ROLE_ADMIN');


