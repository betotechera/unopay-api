create table payment_remittance (
    id VARCHAR(256) PRIMARY KEY,
    issuer_id varchar(256) not null,
    issuer_bank_code varchar(256) not null,
    remittance_number varchar(256) not null,
    service_type varchar(256) not null,
    transfer_option varchar(150) not null,
    operation_type varchar(150) not null,
    occurrence_code varchar(256)
    created_date_time TIMESTAMP not null,
    submission_date_time TIMESTAMP,
    situation varchar(50) not null,
    submission_return_date_time TIMESTAMP,
    version integer,
    constraint fk_remittance_bank foreign key(bank_bacen_code) references bank(bacen_code),
    constraint fk_remittance_issuer foreign key(issuer_id) references issuer(id)
);

COMMENT ON TABLE payment_remittance IS 'Remessa de Pagamento';
COMMENT ON COLUMN payment_remittance.id IS 'Chave Identificação do Registro';
COMMENT ON COLUMN payment_remittance.issuer_id IS 'Chave Identificação da IP Emissora';
COMMENT ON COLUMN payment_remittance.issuer_bank_code IS 'Código do banco da conta pagamento da Instituição de Pagamento emissora';
COMMENT ON COLUMN payment_remittance.remittance_number IS 'Número sequencial único da remessa';
COMMENT ON COLUMN payment_remittance.service_type IS 'Tipo de Serviço FEBRABAN';
COMMENT ON COLUMN payment_remittance.transfer_option IS 'Forma de Lançamento FEBRABAN';
COMMENT ON COLUMN payment_remittance.operation_type IS ' Tipo de operação FEBRABAN';
COMMENT ON COLUMN payment_remittance.occurrence_code IS ' Código de ocorrência do banco';
COMMENT ON COLUMN payment_remittance.created_date_time IS 'Data que a remessa foi criada';
COMMENT ON COLUMN payment_remittance.submission_date_time IS 'Data em que foi gerada o arquivo CNAB da remessa para o banco ';
COMMENT ON COLUMN payment_remittance.submission_return_date_time IS 'Data em que foi recebido o arquivo CNAB de retorno da remessa para o banco ';
COMMENT ON COLUMN payment_remittance.situation IS 'Situação do Lote';
COMMENT ON COLUMN payment_remittance.version IS 'Versao atual do registro';

create table payment_remittance_item (
    id VARCHAR(256) PRIMARY KEY,
    payment_remittance_id varchar(256),
    establishment_id varchar(256) not null,
    establishment_bank_code varchar(256) not null,
    value decimal(*,2) not null,
    situation varchar(150) not null,
    occurrence_code varchar(100) not null,
    version integer,
    constraint fk_remittance_item foreign key(payment_remittance_id) references payment_remittance(id),
    constraint fk_remittance_item_estab foreign key(establishment_id) references establishment(id)
);

COMMENT ON TABLE payment_remittance_item IS 'Item de Remessa de Pagamento';
COMMENT ON COLUMN payment_remittance_item.id IS 'ID Item de Remessa de pagamento';
COMMENT ON COLUMN payment_remittance_item.payment_remittance_id IS 'Chave Identificação da Remessa de pagamento';
COMMENT ON COLUMN payment_remittance_item.establishment_id IS 'Chave Identificação do Estabelecimento';
COMMENT ON COLUMN payment_remittance_item.establishment_bank_code IS 'Código do banco da conta bancária do estabelecimento';
COMMENT ON COLUMN payment_remittance_item.value IS 'Valor Total do item de remessa , soma dos valores dos lotes encontrados';
COMMENT ON COLUMN payment_remittance_item.situation IS 'Situação da remessa';
COMMENT ON COLUMN payment_remittance_item.occurrence_code IS 'Código de ocorrência do banco';
COMMENT ON COLUMN payment_remittance_item.version IS 'Versao atual do registro';

insert into AUTHORITY(name, description) values('ROLE_LIST_PAYMENT_REMITTANCE','Permite listar remessa de pagamentos.');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_PAYMENT_REMITTANCE','Permite gerenciar remessa de pagamentos.');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_PAYMENT_REMITTANCE', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_PAYMENT_REMITTANCE', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');

ALTER TABLE batch_closing ADD CONSTRAINT fk_batch_payment FOREIGN KEY(payment_id) REFERENCES payment_remittance_item(id);