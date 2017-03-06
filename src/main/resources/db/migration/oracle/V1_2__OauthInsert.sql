-- pass secret
insert into oauth_client_details(client_id,client_secret,resource_ids,scope,authorized_grant_types,authorities,additional_information,access_token_validity,refresh_token_validity)
values ('unovation','a2ca3855f39f2fa6c0d553927ded0fc95b88e9672c173da32902d4cdb17f30eedcda8046e9ebca39','uaa','read,write','client_credentials,password,refresh_token,facebook,google','ROLE_CLIENT', '{"organization": "1"}',900,900);


insert into oauth_user_details(id, email, password, shop_id)
values('1', 'ti@unovation.com.br', '25449269c801e35004f0eb163ee00df067ff6e4f95926c686dcab687ca4ae1f3c39cad8627ab19d9', '1');


insert into oauth_user_authorities(user_id, authority) values('1', 'ROLE_ADMIN');

insert into authority(name, description) values('ROLE_ADMIN', 'Permite acesso completo ao sistema');