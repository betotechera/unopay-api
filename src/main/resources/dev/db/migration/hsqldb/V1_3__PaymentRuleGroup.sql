create table payment_rule_group (
  id VARCHAR(256) PRIMARY KEY,
      name VARCHAR(50),
     code VARCHAR(50),
     purpose VARCHAR(50),
     scope VARCHAR(50),
     user_relationship VARCHAR(50),
     version integer
);

create unique index payment_rule_group_uk on payment_rule_group(code);
Insert into AUTHORITY(name, description) values('ROLE_LIST_PAYMENT_RULE_GROUP','Permite listar Arranjos de Pagamento');
Insert into AUTHORITY(name, description) values('ROLE_MANAGE_PAYMENT_RULE_GROUP','Permite gerenciar Arranjos de Pagamento');

ALTER TABLE oauth_user_details ADD payment_rule_group_id VARCHAR(256);
ALTER TABLE oauth_user_details ADD CONSTRAINT fk_user_details_rule_group FOREIGN KEY(payment_rule_group_id) REFERENCES payment_rule_group(id);

