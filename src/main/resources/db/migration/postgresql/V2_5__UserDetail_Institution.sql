ALTER TABLE institution DROP COLUMN payment_rule_group_id;
alter table payment_rule_group ADD institution_id varchar(256);
alter table oauth_user_details drop column  payment_rule_group_id;

ALTER TABLE payment_rule_group ADD CONSTRAINT fk_prg_institution FOREIGN KEY(institution_id) REFERENCES institution(id);

INSERT INTO address(id,zip_code,street_name,street_number,complement,district,city,state,version) VALUES ('95a1f103-a028-434e-a7c2-9c9a922484fb','05305001','Av. Imperatriz Leopoldina','957', 'Sala 802','Vila Leopoldina', 'Sao Paulo','SP',0);
INSERT INTO legal_person_detail(id,fantasy_name,responsible_name,responsible_email,responsible_document_type,responsible_document_number,type,activity,version) VALUES('8bdc799d-6d92-4e74-9d1d-56033261a47f','Unovation TI', 'Diogo Techera','diogo@unovation.com.br','CPF','11082055778','MICRO','SCIENTIFIC',0);
INSERT INTO person (id,name,type,document_type,document_number,telephone,address_id,legal_person_detail_id,version) VALUES ('76880975-b7e0-4f23-b0c1-9f588827e5b5','Unovation','LEGAL','CNPJ','24122925000173','11997601984','95a1f103-a028-434e-a7c2-9c9a922484fb','8bdc799d-6d92-4e74-9d1d-56033261a47f',0);
INSERT INTO institution(id,person_id,version) VALUES ('a2f41c25-5d25-4620-a303-c921a9553f0a','76880975-b7e0-4f23-b0c1-9f588827e5b5',0);
UPDATE user_type SET name = 'INSTITUIDOR',description = 'Instituidor de Arranjo de pagamento' WHERE id = '92af025e-1580-4c49-8246-f965e48c5721';
UPDATE OAUTH_USER_DETAILS SET institution_id = 'a2f41c25-5d25-4620-a303-c921a9553f0a' WHERE id = '643f406a-0cfa-422c-bea2-e80dfc63af15';