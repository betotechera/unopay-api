create table payment_rule_group (
    id VARCHAR(256) PRIMARY KEY,
     name VARCHAR(50),
     code VARCHAR(50),
     purpose VARCHAR(50),
     scope VARCHAR(50),
     user_relationship VARCHAR(50),
     version integer,
     CONSTRAINT payment_rule_group_uk UNIQUE (code)

);

Insert into AUTHORITY(name, description) values('ROLE_LIST_PAYMENT_RULE_GROUP','Permite listar Arranjos de Pagamento');
Insert into AUTHORITY(name, description) values('ROLE_MANAGE_PAYMENT_RULE_GROUP','Permite gerenciar Arranjos de Pagamento');

insert into PAYMENT_RULE_GROUP(id,name,code,purpose,scope,USER_RELATIONSHIP) values('66eac89e-10f8-11e7-93ae-92361f002671','Unovation','1234','BUY','DOMESTIC','PREPAID')

ALTER TABLE oauth_user_details ADD payment_rule_group_id VARCHAR(256);
ALTER TABLE oauth_user_details ADD CONSTRAINT fk_user_details_rule_group FOREIGN KEY(payment_rule_group_id) REFERENCES payment_rule_group(id);

UPDATE OAUTH_USER_DETAILS set payment_rule_group_id = '66eac89e-10f8-11e7-93ae-92361f002671' where id = '643f406a-0cfa-422c-bea2-e80dfc63af15'