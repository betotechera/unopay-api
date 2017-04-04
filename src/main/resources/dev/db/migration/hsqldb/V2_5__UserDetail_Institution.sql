ALTER TABLE institution DROP COLUMN payment_rule_group_id;
alter table payment_rule_group ADD institution_id varchar(256);
alter table oauth_user_details drop column  payment_rule_group_id;

ALTER TABLE payment_rule_group ADD CONSTRAINT fk_prg_institution FOREIGN KEY(institution_id) REFERENCES institution(id);

INSERT INTO address(id,zip_code,street_name,street_number,complement,district,city,state,version) VALUES ('2','05305001','Av. Imperatriz Leopoldina','957', 'Sala 802','Vila Leopoldina', 'Sao Paulo','SP',0);
INSERT INTO legal_person_detail(id,fantasy_name,responsible_name,responsible_email,responsible_document_type,responsible_document_number,type,activity,version) VALUES('2','Unovation TI', 'Diogo Techera','diogo@unovation.com.br','CPF','11082055778','MICRO','SCIENTIFIC',0);
INSERT INTO person (id,name,type,document_type,document_number,telephone,address_id,legal_person_detail_id,version) VALUES ('2','Unovation','LEGAL','CNPJ','24122925000173','11997601984','2','2',0);
INSERT INTO institution(id,person_id,version) VALUES ('2','2',0);
UPDATE user_type SET name = 'INSTITUIDOR',description = 'Instituidor de Arranjo de pagamento' WHERE id = '2';
UPDATE OAUTH_USER_DETAILS SET institution_id = '2' WHERE id = '1';