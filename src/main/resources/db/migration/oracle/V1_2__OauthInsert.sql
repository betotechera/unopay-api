-- pass secret
insert into oauth_client_details(client_id,client_secret,resource_ids,scope,authorized_grant_types,authorities,additional_information,access_token_validity,refresh_token_validity)
values ('unovation','c7bfc8f1ce2b3bcbdec9ae12dc4f04af0d9d1ad0253dcdcf8205b0a31bd4c6e5c4494095a762b79b','uaa','read,write','client_credentials,password,refresh_token,facebook,google','ROLE_CLIENT', '{"organization": "1"}',900,900);


insert into oauth_user_details(id, email, password, version)
values('1', 'ti@unovation.com.br', 'c7bfc8f1ce2b3bcbdec9ae12dc4f04af0d9d1ad0253dcdcf8205b0a31bd4c6e5c4494095a762b79b', 0);


insert into oauth_user_authorities(user_id, authority) values('1', 'ROLE_ADMIN');

insert into authority(name, description) values('ROLE_ADMIN', 'Permite acesso completo ao sistema');