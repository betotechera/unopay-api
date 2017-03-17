-- pass secret
insert into oauth_client_details(client_id,client_secret,resource_ids,scope,authorized_grant_types,authorities,additional_information,access_token_validity,refresh_token_validity)
values ('unovation','c7bfc8f1ce2b3bcbdec9ae12dc4f04af0d9d1ad0253dcdcf8205b0a31bd4c6e5c4494095a762b79b','uaa','read,write','client_credentials,password,refresh_token,facebook,google','ROLE_CLIENT', '{"organization": "1"}',900,900);


insert into oauth_user_details(id, email, password, version)
values('643f406a-0cfa-422c-bea2-e80dfc63af15', 'ti@unovation.com.br', 'a023dceb3b15e9b9586bc149e02b57d0f0b6929db9b84bfcaa607216d8fe638c4faf2ca543d2304a', 0);

insert into oauth_groups(id, group_name, description, version) values('99bf9ba6-75e4-4109-b5be-e4858f3f68b2','admin', 'Permite acesso completo ao sistema', 0);

insert into authority(name, description) values('ROLE_ADMIN', 'Permite acesso completo ao sistema');

insert into oauth_group_members(user_id, group_id) values('643f406a-0cfa-422c-bea2-e80dfc63af15', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');

insert into oauth_group_authorities(authority, group_id) values('ROLE_ADMIN', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');

insert into oauth_user_authorities(user_id, authority) values('643f406a-0cfa-422c-bea2-e80dfc63af15', 'ROLE_ADMIN');

