create table remittance_payer (
    id VARCHAR(256) PRIMARY KEY,
    bank_code integer not null,
    bank_agreement_number varchar(256) not null,
    document_number varchar(256),
    agency varchar(20) not null,
    agency_digit varchar(10),
    account_number varchar(50) not null,
    account_number_digit varchar(10),
    name varchar(256) not null,
    bank_name varchar(256) not null,
    zip_code VARCHAR(50),
    street_name VARCHAR(250),
    street_number VARCHAR(50),
    complement VARCHAR(50),
    district VARCHAR(50),
    city VARCHAR(50),
    state VARCHAR(50),
    version integer,
    constraint fk_remittance_payer_bank foreign key(bank_code) references bank(bacen_code),
);

COMMENT ON TABLE remittance_payer IS 'Pagador da remessa ';
COMMENT ON COLUMN remittance_payer.id IS 'Chave Identificação do Registro';
COMMENT ON COLUMN remittance_payer.bank_code IS 'Código do banco da conta pagamento';
COMMENT ON COLUMN remittance_payer.document_number IS 'documento de identificacao do pagador';
COMMENT ON COLUMN remittance_payer.bank_agreement_number IS 'codigo de conveio com o banco';
COMMENT ON COLUMN remittance_payer.agency IS 'agencia do pagador';
COMMENT ON COLUMN remittance_payer.agency_digit IS 'digito da agencia do pagador';
COMMENT ON COLUMN remittance_payer.account_number IS 'numero da conta do pagador';
COMMENT ON COLUMN remittance_payer.account_number_digit IS 'digito da conta do pagador';
COMMENT ON COLUMN remittance_payer.name IS 'nome do pagador';
COMMENT ON COLUMN remittance_payer.bank_name IS 'nome do banco do pagador';
COMMENT ON COLUMN remittance_payer.zip_code IS 'cep do pagador';
COMMENT ON COLUMN remittance_payer.street_name IS 'logradouro do pagador';
COMMENT ON COLUMN remittance_payer.street_number IS 'numero do endereco do pagador';
COMMENT ON COLUMN remittance_payer.complement IS 'complemento do endereco';
COMMENT ON COLUMN remittance_payer.district IS 'bairro do endereco do pagador';
COMMENT ON COLUMN remittance_payer.city IS 'cidade do endereco do pagador';
COMMENT ON COLUMN remittance_payer.state IS 'estado do endereco do pagador';
COMMENT ON COLUMN remittance_payer.version IS 'Versao atual do registro';

create table payment_remittance (
    id VARCHAR(256) PRIMARY KEY,
    payer_id varchar(256) not null,
    remittance_number varchar(256) not null,
    service_type varchar(256) not null,
    operation_type varchar(150) not null,
    occurrence_code varchar(256),
    created_date_time TIMESTAMP not null,
    submission_date_time TIMESTAMP,
    situation varchar(150) not null,
    cnab_uri varchar(255),
    submission_return_date_time TIMESTAMP,
    version integer,
    constraint fk_remittance_payer foreign key(payer_id) references remittance_payer(id)
);

COMMENT ON TABLE payment_remittance IS 'Remessa de Pagamento';
COMMENT ON COLUMN payment_remittance.id IS 'Chave Identificação do Registro';
COMMENT ON COLUMN payment_remittance.payer_id IS 'Chave Identificação do pagador';
COMMENT ON COLUMN payment_remittance.remittance_number IS 'Número sequencial único da remessa';
COMMENT ON COLUMN payment_remittance.service_type IS 'Tipo de Serviço FEBRABAN';
COMMENT ON COLUMN payment_remittance.operation_type IS ' Tipo de operação FEBRABAN';
COMMENT ON COLUMN payment_remittance.occurrence_code IS ' Código de ocorrência do banco';
COMMENT ON COLUMN payment_remittance.created_date_time IS 'Data que a remessa foi criada';
COMMENT ON COLUMN payment_remittance.submission_date_time IS 'Data em que foi gerada o arquivo CNAB da remessa para o banco ';
COMMENT ON COLUMN payment_remittance.submission_return_date_time IS 'Data em que foi recebido o arquivo CNAB de retorno da remessa para o banco ';
COMMENT ON COLUMN payment_remittance.situation IS 'Situação do Lote';
COMMENT ON COLUMN payment_remittance.version IS 'Versao atual do registro';

create table remittance_payee (
    id VARCHAR(256) PRIMARY KEY,
    agency varchar(20) not null,
    bank_code integer not null,
    payer_bank_code integer not null,
    agency_digit varchar(10),
    account_number varchar(50) not null,
    account_number_digit varchar(10),
    name varchar(256) not null,
    document_number varchar(256),
    zip_code VARCHAR(50),
    street_name VARCHAR(250),
    street_number VARCHAR(50),
    complement VARCHAR(50),
    district VARCHAR(50),
    city VARCHAR(50),
    state VARCHAR(50),
    version integer,
     constraint fk_remitt_payer_payee_bank foreign key(payer_bank_code) references bank(bacen_code),
     constraint fk_remitt_payee_bank foreign key(bank_code) references bank(bacen_code)
);

COMMENT ON TABLE remittance_payee IS 'recebedor da remessa ';
COMMENT ON COLUMN remittance_payee.id IS 'Chave Identificação do Registro';
COMMENT ON COLUMN remittance_payee.payer_bank_code IS 'Código do banco da conta pagamento';
COMMENT ON COLUMN remittance_payee.bank_code IS 'Código do banco da conta bancária do recebedor';
COMMENT ON COLUMN remittance_payee.agency IS 'agencia do recebedor';
COMMENT ON COLUMN remittance_payee.agency_digit IS 'digito da agencia do recebedor';
COMMENT ON COLUMN remittance_payee.account_number IS 'numero da conta do recebedor';
COMMENT ON COLUMN remittance_payee.account_number_digit IS 'digito da conta do recebedor';
COMMENT ON COLUMN remittance_payee.name IS 'nome do recebedor';
COMMENT ON COLUMN remittance_payee.document_number IS 'documento de identificacao do recebedor';
COMMENT ON COLUMN remittance_payee.zip_code IS 'cep do recebedor';
COMMENT ON COLUMN remittance_payee.street_name IS 'logradouro do recebedor';
COMMENT ON COLUMN remittance_payee.street_number IS 'numero do endereco do recebedor';
COMMENT ON COLUMN remittance_payee.complement IS 'complemento do endereco';
COMMENT ON COLUMN remittance_payee.district IS 'bairro do endereco do recebedor';
COMMENT ON COLUMN remittance_payee.city IS 'cidade do endereco do recebedor';
COMMENT ON COLUMN remittance_payee.state IS 'estado do endereco do recebedor';
COMMENT ON COLUMN remittance_payee.version IS 'Versao atual do registro';

create table payment_remittance_item (
    id VARCHAR(256) PRIMARY KEY,
    payment_remittance_id varchar(256),
    payee_id varchar(256) not null,
    transfer_option varchar(150) not null,
    value decimal(20,2) not null,
    situation varchar(150) not null,
    occurrence_code varchar(100),
    version integer,
    constraint fk_remittance_item foreign key(payment_remittance_id) references payment_remittance(id),
    constraint fk_remittance_item_payee foreign key(payee_id) references remittance_payee(id)
);

COMMENT ON TABLE payment_remittance_item IS 'Item de Remessa de Pagamento';
COMMENT ON COLUMN payment_remittance_item.id IS 'ID Item de Remessa de pagamento';
COMMENT ON COLUMN payment_remittance_item.transfer_option IS 'Forma de Lançamento FEBRABAN';
COMMENT ON COLUMN payment_remittance_item.payment_remittance_id IS 'Chave Identificação da Remessa de pagamento';
COMMENT ON COLUMN payment_remittance_item.payee_id IS 'Chave Identificação do recebedor';
COMMENT ON COLUMN payment_remittance_item.value IS 'Valor Total do item de remessa , soma dos valores dos lotes encontrados';
COMMENT ON COLUMN payment_remittance_item.situation IS 'Situação da remessa';
COMMENT ON COLUMN payment_remittance_item.occurrence_code IS 'Código de ocorrência do banco';
COMMENT ON COLUMN payment_remittance_item.version IS 'Versao atual do registro';

insert into AUTHORITY(name, description) values('ROLE_LIST_PAYMENT_REMITTANCE','Permite listar remessa de pagamentos.');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_PAYMENT_REMITTANCE','Permite gerenciar remessa de pagamentos.');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_PAYMENT_REMITTANCE', '1');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_PAYMENT_REMITTANCE', '1');

ALTER TABLE batch_closing ADD CONSTRAINT fk_batch_payment FOREIGN KEY(payment_id) REFERENCES payment_remittance_item(id);