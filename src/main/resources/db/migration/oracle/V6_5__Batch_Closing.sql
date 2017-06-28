create table batch_closing (
    id VARCHAR(256) PRIMARY KEY,
    establishment_id varchar(256) not null,
    issuer_id varchar(256) not null,
    accredited_network_id varchar(256) not null,
    hirer_id varchar(256) not null,
    issue_invoice char(1) not null,
    closing_date_time TIMESTAMP not null,
    value decimal(20,2) not null,
    period varchar(256) not null,
    payment_release_date_time TIMESTAMP NOT NULL,
    situation varchar(50) not null,
    payment_date_time TIMESTAMP NOT NULL,
    payment_id varchar(256),
    version integer,
    constraint fk_batch_est foreign key(establishment_id) references establishment(id),
    constraint fk_batch_issuer foreign key(issuer_id) references issuer(id),
    constraint fk_batch_network foreign key(accredited_network_id) references accredited_network(id),
    constraint fk_batch_hirer foreign key(hirer_id) references hirer(id)
);

COMMENT ON TABLE batch_closing IS 'Fechamento de lote';
COMMENT ON COLUMN batch_closing.id IS 'Chave Identificação do Registro';
COMMENT ON COLUMN batch_closing.accredited_network_id IS 'Chave Identificação da IP Credenciadora';
COMMENT ON COLUMN batch_closing.issuer_id IS 'Chave Identificação da IP Emissora';
COMMENT ON COLUMN batch_closing.establishment_id IS 'Chave Identificação do Estabelecimento';
COMMENT ON COLUMN batch_closing.hirer_id IS 'Chave Identificação do Contratante';
COMMENT ON COLUMN batch_closing.issue_invoice IS 'campo emite Nota Fiscal do cadastro de contrato associado a autorização';
COMMENT ON COLUMN batch_closing.closing_date_time IS 'Data de fechamento calculada conforme período de movimentação cadastrado para o estabelecimento';
COMMENT ON COLUMN batch_closing.period IS 'Período de movimentação cadastrado na tabela do estabelecimento na data de processamento';
COMMENT ON COLUMN batch_closing.payment_release_date_time IS 'Calculada pela aplicação somando a quantidade de dias do prazo de pagamento da tabela de estabelecimento';
COMMENT ON COLUMN batch_closing.value IS 'Valor Total do Lote, soma dos valores das autorizações encontradas';
COMMENT ON COLUMN batch_closing.situation IS 'Situação do Lote';
COMMENT ON COLUMN batch_closing.payment_date_time IS 'Data registrada pelo processo de pagamento, igual a data de fechamento + valor do campo prazo de pagamento do fechamento da tabela de estabelecimento';
COMMENT ON COLUMN batch_closing.payment_id IS 'Identificador do registro de pagamento quando efetuado';
COMMENT ON COLUMN batch_closing.version IS 'Versao atual do registro';

create table batch_closing_item (
    id VARCHAR(256) PRIMARY KEY,
    batch_closing_id varchar(256),
    service_authorize_id varchar(256) not null,
    document_number_invoice varchar(256) not null,
    invoice_number varchar(256),
    invoice_document_situation varchar(100) not null,
    invoice_document_uri varchar(256),
    issue_invoice_type varchar(100) not null,
    version integer,
    constraint fk_batch_item foreign key(batch_closing_id) references batch_closing(id),
    constraint fk_batch_item_auth foreign key(service_authorize_id) references service_authorize(id)
);

COMMENT ON TABLE batch_closing_item IS 'Item de fechamento de Lote';
COMMENT ON COLUMN batch_closing_item.id IS 'ID Item de Lote';
COMMENT ON COLUMN batch_closing_item.batch_closing_id IS 'Chave Identificação do Fechamento de lote';
COMMENT ON COLUMN batch_closing_item.service_authorize_id IS 'Chave Identificação da Autorização';
COMMENT ON COLUMN batch_closing_item.document_number_invoice IS 'Identificador do CNPJ contratante relacionado ao contrato da autorização que deve ser emitida a Nota fiscal';
COMMENT ON COLUMN batch_closing_item.invoice_number IS 'Número da NF gerada pelo estabelecimento';
COMMENT ON COLUMN batch_closing_item.invoice_document_situation IS 'Número da NF gerada pelo estabelecimento';
COMMENT ON COLUMN batch_closing_item.invoice_document_uri IS 'Uri do documento da nota fiscal em formato XML ou PDF';
COMMENT ON COLUMN batch_closing_item.issue_invoice_type IS 'Tipo de Emissão Nota Fiscal';
COMMENT ON COLUMN batch_closing_item.version IS 'Versao atual do registro';

insert into AUTHORITY(name, description) values('ROLE_LIST_BATCH_CLOSING','Permite listar fechamentos de lote.');
insert into AUTHORITY(name, description) values('ROLE_MANAGE_BATCH_CLOSING','Permite gerenciar fechamentos de lote.');

insert into oauth_group_authorities(authority, group_id) values('ROLE_LIST_BATCH_CLOSING', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');
insert into oauth_group_authorities(authority, group_id) values('ROLE_MANAGE_BATCH_CLOSING', '99bf9ba6-75e4-4109-b5be-e4858f3f68b2');

alter table service_authorize add batch_closing_date_time timestamp;