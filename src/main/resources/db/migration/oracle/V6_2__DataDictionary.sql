COMMENT ON COLUMN authority.name IS 'Nome da Permissao de Acesso ao sistema';
COMMENT ON COLUMN authority.description IS 'Descriçao da permissao de acesso ao sistema';

COMMENT ON COLUMN user_type.name IS 'Nome do tipo de usuario';
COMMENT ON COLUMN user_type.description IS 'Descriçao do tipo de usuario';

COMMENT ON COLUMN oauth_user_details.name IS 'Nome do usuario do sistema';
COMMENT ON COLUMN oauth_user_details.email IS 'Email do usuario do sistema';
COMMENT ON COLUMN oauth_user_details.type IS 'Identificador do tipo de usuario associado ao usuario';
COMMENT ON COLUMN oauth_user_details.password IS 'Senha criptografada do usuario';

COMMENT ON COLUMN oauth_groups.group_name IS 'Nome do grupo de Acesso';
COMMENT ON COLUMN oauth_groups.description IS 'Descriçao do Perfil de Acesso';
COMMENT ON COLUMN oauth_groups.user_type IS 'Identificador do tipo de usuario associado ao Perfil do Acesso';

COMMENT ON COLUMN payment_rule_group.name IS 'Nome do Arranjo de pagamento cadastrado no Banco Central';
COMMENT ON COLUMN payment_rule_group.code IS 'Código do Arranjo de pagamento – Código de identifcação do Arranjo de pagamento no cadastro do Banco Central';
COMMENT ON COLUMN payment_rule_group.purpose IS 'Propósito do arranjo de pagamento – com as seguintes opções: BUY(Compra) – Quando o serviço didae pagamento do arranjo estiver vinculado a liquidação de determinada obrigação; ou TRANSFER(Tranferencia) Quando o serviço de pagamento do arranjo estiver necessariamente vinculado a liquidação de determinada obrigação.';
COMMENT ON COLUMN payment_rule_group.scope IS 'Abrangência territorial. Campo obrigatório que define a abrangência do arranjo. O usuário irá selecionar entre duas opções: DOMESTIC(Doméstico) – Quando o instrumento de pagamento disciplinado do arranjo só puder ser emitido e utilizado em território nacional; ou INTERNATIONAL(Internacional) – Quando o instrumento de pagamento do arranjo for emitido em território nacional para ser utilizado em outros países ou for emitido fora do território nacional para ser utilizado no país.';
COMMENT ON COLUMN payment_rule_group.user_relationship IS 'Relacionamento com usuários finais – Campo obrigatório. O usuário irá selecionar entre as seguintes quatro opções:PREPAID(Conta de pagamento Pre-paga), POSTPAID(Conta de pagamento Pos-paga) DEPOSIT(Conta de deposito à vista) ou EVENTUAL(Relacionamento eventual).';

COMMENT ON COLUMN legal_person_detail.fantasy_name IS 'Nome fantasia da Pessoa Jurídca';
COMMENT ON COLUMN legal_person_detail.responsible_name IS 'Nome abreviado do responsével pelas informações ao BACEN';
COMMENT ON COLUMN legal_person_detail.responsible_email IS 'E-mail principaldo do responsável. Descrição do endereço principal de correio eletrônico';
COMMENT ON COLUMN legal_person_detail.responsible_document_type IS 'Tipo de Documento do responsável - Conforme Tabela 3 da Especificação funcional';
COMMENT ON COLUMN legal_person_detail.responsible_document_number IS 'Número do documento do responsável Numero de identificação do documento';
COMMENT ON COLUMN legal_person_detail.responsible_registry_entity IS 'Órgão Expedidor - Órgão expedidor do documento Conforme Tabela 2 da Especificação funcional';
COMMENT ON COLUMN legal_person_detail.type IS 'Forma da Criação. LIMITED_PARTNERSHIP(Sociedade por quotas de responsabilidade limitada). STOCK_COMPANY(Sociedade por ações - S.A). COOPERATIVE_SOCIETY(Sociedade simples / cooperativa). MICRO(Micro empresa de pequeno porte). FREELANCE(Autonomo)';
COMMENT ON COLUMN legal_person_detail.activity IS 'Atividade Principal da Pessoa Jurídica. Valores possíveis no enum CompanyActivity';
COMMENT ON COLUMN legal_person_detail.creation_date IS 'Data de Fundação da Pessoa Jurídica';

COMMENT ON COLUMN address.zip_code IS 'Número do CEP - Código de endereçamento postal';
COMMENT ON COLUMN address.street_name IS 'Nome da Rua - Nome do logradouro do endereço';
COMMENT ON COLUMN address.street_number IS 'Número - Número no logradouro do endereço';
COMMENT ON COLUMN address.complement IS 'Complemento - Complemento do endereço do logradouro';
COMMENT ON COLUMN address.district IS 'Bairro - Nome do bairro do endereço';
COMMENT ON COLUMN address.city IS 'Cidade - Nome da cidade do endereço';
COMMENT ON COLUMN address.state IS 'Estado - Nome do estado do endereço';
COMMENT ON COLUMN address.latitude IS 'Latitude do endereço conforme Google MAPS';
COMMENT ON COLUMN address.longitude IS 'Longitude do endereço conforme Google MAPS';

COMMENT ON COLUMN person.name IS 'Nome da peso física ou Razão social da pessoa jurídica';
COMMENT ON COLUMN person.type IS 'Tipo de Pessoa - Assume PHYSICAL se pessoa física ou LEGAL se pessoa Jurídica';
COMMENT ON COLUMN person.document_type IS 'Tipo de Documento da pessoa - Conforme Tabela 3 da Especificação funcional';
COMMENT ON COLUMN person.document_number IS 'Número do documento da pessoa - Numero de identificação do documento';
COMMENT ON COLUMN person.registry_entity IS 'Órgão Expedidor - Órgão expedidor do documento Conforme Tabela 2 da Especificação funcional';
COMMENT ON COLUMN person.telephone IS 'Número de Telefone - Número do telefone da pessoa';

COMMENT ON COLUMN bank_account.bank_bacen_code IS 'Código do Banco - Número de identificação do banco, definido pelo  Banco Central ';
COMMENT ON COLUMN bank_account.agency IS 'Numero da agencia - Numero de identificação da agencia bancária';
COMMENT ON COLUMN bank_account.agency_digit IS 'DV agencia - Numero do dígito verificador da agencia bancária';
COMMENT ON COLUMN bank_account.account_number IS 'Numero Conta Corrente - Numero de identificação da conta bancária';
COMMENT ON COLUMN bank_account.account_number_digit IS 'DV da Conta Corrente - Numero do dígito verificador da conta bancária.';
COMMENT ON COLUMN bank_account.account_type IS 'Tipo de Conta Corrente - Assume valores P - Poupança ou C - Conta corrente';

COMMENT ON COLUMN payment_bank_account.authorize_transfer IS 'Autorizar DOC/TED –Valor percentual da taxa de administração cobrada pelo emissor sobre as movimnetações financeiras de crédito inseridos pelos contratantes e parceiros.';
COMMENT ON COLUMN payment_bank_account.deposit_period IS 'Período de depósito – Indica o periodo de realização dos depósitos dos pagamentos a serem efetuados, com as opções de DAILY(Diário), WEEKLY(Semanal), BIWEEKLY(Quinzenal) e MONTHLY(Mensal). ';
COMMENT ON COLUMN payment_bank_account.post_paid_payment_days IS 'Dias de pagamento pós-pago – Quantidade de dias para pagamentos aos estabelecimentos, parceiros e contratados das movimentações financeiras realizdas com instrumentos de pagamento pós-pagos.';
COMMENT ON COLUMN payment_bank_account.pre_paid_payment_days IS 'Dias de pagamento pré-pago – Quantidade de dias para pagamentos aos estabelecimentos, parceiros e contratados das movimentações financeiras realizadas com instrumentos de pagamento pré-´pagos.';

COMMENT ON COLUMN issuer.fee IS 'Taxa de administração MDR – Valor percentual da taxa de administração cobrada pelo emissor sobre as movimnetações financeiras de crédito inseridos pelos contratantes e parceiros.';

COMMENT ON COLUMN accredited_network.merchant_discount_rate IS 'Taxa de administração MDR – Valor percentual da taxa de administração cobrada pelo emissor aplicada sobre cada operação de autorização e pagamentos realizados ao estabelecimentos.';
COMMENT ON COLUMN accredited_network.type IS 'Identifica o tipo de rede controlada pela Instituição de pagamento credenciadora, com as opções SUPPLY("Rede de Abastecimento/Quitação de Frete"), TOLL("Rede de Pedágio Eletrônico");';
COMMENT ON COLUMN accredited_network.movement_period IS 'Período de movimentação – Indica o periodo de realização dos dos pagamentos a serem efetuados,com as opções DAILY(Diário), WEEKLY(Semanal), BIWEEKLY(Quinzenal) e MONTHLY(Mensal).';
COMMENT ON COLUMN accredited_network.authorize_transfer IS 'Autorizar DOC/TED – Indica se o Emissor irá descontar do estabelecimento as tarifas bancárias bancárias dos pagamentos realizados através de DOC ou TED.';
COMMENT ON COLUMN accredited_network.closing_payment_days IS 'Período de depósito – Indica o periodo de realização dos depósitos dos pagamentos a serem efetuados com as opções DAILY(Diário), WEEKLY(Semanal), BIWEEKLY(Quinzenal) e MONTHLY(Mensal).';
COMMENT ON COLUMN accredited_network.minimum_deposit_value IS 'Valor mínimo de depósito – Define o valor mínimo para que um deposito de pagamento possa ser processado pela plataforma';
COMMENT ON COLUMN accredited_network.invoice_receipt_period IS 'Tipo de Recebimento de NFe – indica a forma que o estabelecimento irá enviar a Nota fiscal dos serviços prestados para recebimentos dos valores devidos com os seguintes valores previstos: XML – Recebe XML NF ou MANUAL – Recebimento manual NF;';
COMMENT ON COLUMN accredited_network.invoice_receipt_type IS 'Período de Recebimento NFe – Indica a periodicidade em que o estabelecimento irá enviar as NF dos serviços prestados, com as opções DAILY(Diário), WEEKLY(Semanal), BIWEEKLY(Quinzenal) e MONTHLY(Mensal).';

COMMENT ON COLUMN service.type IS 'Tipo de Serviço – Qualifica o serviço em tipos para permitir fluxos internos de processamento a serem assumidos pela aplicação , com as opções de FUEL_ALLOWANCE("Vale Abastecimento"),FREIGHT("Frete"), FREIGHT_RECEIPT("Quitaçao de Frete"), ELECTRONIC_TOLL("Pedágio eletrônico")';
COMMENT ON COLUMN service.code IS 'Código identificador do serviço.';
COMMENT ON COLUMN service.name IS 'Nome do serviço';
COMMENT ON COLUMN service.fee_val IS 'Valor da Tarifa do Serviço - Valor em moeda corrente da tarifa do serviço a ser cobrada pelo emissor de um estabelecimento.';
COMMENT ON COLUMN service.fee_percent IS 'Percentual da Tarifa Serviço- Valor percentual da tarifa do serviço a ser cobrada pelo emissor sobre o valor de serviço prestado por um estabelecimento na autorização de um serviço.';

COMMENT ON COLUMN event.ncm_code IS 'Código NCM - Nomenclatura Comum do Mercosul e trata-se de um código de oito dígitos estabelecido pelo Governo Brasileiro para identificar a natureza das mercadorias e promover o desenvolvimento do comércio internacional.';
COMMENT ON COLUMN event.name IS 'Nome do evento – Nome descritivo do evento..';
COMMENT ON COLUMN event.request_quantity IS 'Solicita quantidade – Indica se no processo de autorização de um serviços será solicitado a digitação da quantidade consumida do evento.';
COMMENT ON COLUMN event.quantity_unity IS 'Unidade do evento – Define a unidade do evento se Solicita Quantidade estiver marcado. Exemplo: Litros, KG.';

COMMENT ON COLUMN brand_flag.name IS 'Nome da bandeira/marca principal de um estabelecimento.Exemplo SHELL Marca da distribuidora de combustíveis do posto.';
COMMENT ON COLUMN brand_flag.description IS 'Descrição da bandeira/marca principal de um estabelecimento.';

COMMENT ON COLUMN contact.name IS 'Nome do Contato - Descrição do nome do contato';
COMMENT ON COLUMN contact.mail IS 'e-mail principal - Descrição do endereço principal de correio eletrônico';
COMMENT ON COLUMN contact.cell_phone IS 'Numero Telefone - Número do código de Discagem Direta à Distância e numero do telefone';

COMMENT ON COLUMN establishment.type IS 'Tipo de Estabeleciment: SUPPORT_POINT("Ponto de apoio"), SUPPLY_STATION("Posto de abastecimento"),TOLL_STATION("Posto de Pedágio")';
COMMENT ON COLUMN establishment.contact_mail IS 'E-mail de Contato –- Descrição do endereço principal de correio eletrônico de contatao com o estabelecimento';
COMMENT ON COLUMN establishment.invoice_mail IS 'Email de envio NF - Descrição do endereço de correio eletrônico para o envio das Notas fiacsis gerados pela Instituição de pagamento credenciadora ';
COMMENT ON COLUMN establishment.bach_shipment_mail IS 'Email de envio de Lote - Descrição do endereço de correio eletrônico para o envio dos dados de fechamento de lote para pagamento das Notas fiscais cobradas pelo estabelecimento. ';
COMMENT ON COLUMN establishment.alternative_mail IS 'E-mail alternativo – Descrição do endereço alternativo de correio eletrônico de contatao com o estabelecimento ';
COMMENT ON COLUMN establishment.cancellation_tolerance IS 'Tolerância de cancelamento (Minutos) – Tempo permitido para a realização e um cancelamento de uma autorização de serviço autorizada pelo emissor,';
COMMENT ON COLUMN establishment.fee IS 'Taxa de administração MDR – Valor percentual da taxa de administração cobrada pelo emissor aplicada sobre cada operação de autorização e pagamentos realizados ao estabelecimentos';
COMMENT ON COLUMN establishment.logo_uri IS 'Logo – Imagem da logomarca do estabelecimento.';
COMMENT ON COLUMN establishment.operational_contact_id IS 'ID do Contato Operacional do estabelecimento ';
COMMENT ON COLUMN establishment.administrative_contact_id IS 'Nome do Contato Administratuvi do estabelecimento';
COMMENT ON COLUMN establishment.financier_contact_id IS 'Indentificador do contato financeiro do estabelecimento.';
COMMENT ON COLUMN establishment.technical_contact IS ' Nome do Contato técnico do estabelecimento';
COMMENT ON COLUMN establishment.establishment_photo_uri IS ' Foto do estabelecimento – Foto da fachada/área de prestação de serviços (abastecimento) do estabelecimento';
COMMENT ON COLUMN establishment.contract_uri IS 'Contrato – Arquivo digital do contrato firmado entre a Instituiç~çao de pagamento credenciadora com o estabelecimento.';
COMMENT ON COLUMN establishment.movement_period IS 'Período de movimentação – Indica o periodo de realização dos dos pagamentos a serem efetuados,com as opções DAILY(Diário), WEEKLY(Semanal), BIWEEKLY(Quinzenal) e MONTHLY(Mensal).';
COMMENT ON COLUMN establishment.authorize_transfer IS 'Autorizar DOC/TED – Indica se o Emissor irá descontar do estabelecimento as tarifas bancárias bancárias dos pagamentos realizados através de DOC ou TED.';
COMMENT ON COLUMN establishment.closing_payment_days IS 'Período de depósito – Indica o periodo de realização dos depósitos dos pagamentos a serem efetuados com as opções DAILY(Diário), WEEKLY(Semanal), BIWEEKLY(Quinzenal) e MONTHLY(Mensal).';
COMMENT ON COLUMN establishment.minimum_deposit_value IS 'Valor mínimo de depósito – Define o valor mínimo para que um deposito de pagamento possa ser processado pela plataforma';
COMMENT ON COLUMN establishment.invoice_receipt_period IS 'Tipo de Recebimento de NFe – indica a forma que o estabelecimento irá enviar a Nota fiscal dos serviços prestados para recebimentos dos valores devidos com os seguintes valores previstos: XML – Recebe XML NF ou MANUAL – Recebimento manual NF;';
COMMENT ON COLUMN establishment.invoice_receipt_type IS 'Período de Recebimento NFe – Indica a periodicidade em que o estabelecimento irá enviar as NF dos serviços prestados, com as opções DAILY(Diário), WEEKLY(Semanal), BIWEEKLY(Quinzenal) e MONTHLY(Mensal).';

COMMENT ON COLUMN branch.head_office_id IS 'Identificador do Estabelecimento Matriz';
COMMENT ON COLUMN branch.contact_mail IS 'E-mail de Contato –- Descrição do endereço principal de correio eletrônico de contatao com a Filial';
COMMENT ON COLUMN branch.invoice_mail IS 'Email de envio NF - Descrição do endereço de correio eletrônico para o envio das Notas fiacsis gerados pela Instituição de pagamento credenciadora ';
COMMENT ON COLUMN branch.alternative_mail IS 'E-mail alternativo – Descrição do endereço alternativo de correio eletrônico de contatao com a Filial ';
COMMENT ON COLUMN branch.cancellation_tolerance IS 'Tolerância de cancelamento (Minutos) – Tempo permitido para a realização e um cancelamento de uma autorização de serviço autorizada pelo emissor,';
COMMENT ON COLUMN branch.fee IS 'Taxa de administração MDR – Valor percentual da taxa de administração cobrada pelo emissor aplicada sobre cada operação de autorização e pagamentos realizados à Filial';
COMMENT ON COLUMN branch.technical_contact IS ' Nome do Contato técnico da Filial';
COMMENT ON COLUMN branch.branch_photo_uri IS ' Foto da Filial – Foto da fachada/área de prestação de serviços (abastecimento) da Filial';
COMMENT ON COLUMN branch.contract_uri IS 'Contrato – Arquivo digital do contrato firmado entre a Instituiç~çao de pagamento credenciadora com a Filial.';
COMMENT ON COLUMN branch.gathering_channel IS 'Meio de captura – Tipo de equipamento onde será realizada a captura dos dados necessários para o processamento de uma autorização de serviço prestado pela Filial, com as opções WEB,MOBILE,POS';
COMMENT ON COLUMN branch.movement_period IS 'Período de movimentação – Indica o periodo de realização dos dos pagamentos a serem efetuados,com as opções DAILY(Diário), WEEKLY(Semanal), BIWEEKLY(Quinzenal) e MONTHLY(Mensal).';
COMMENT ON COLUMN branch.authorize_transfer IS 'Autorizar DOC/TED – Indica se o Emissor irá descontar da Filial as tarifas bancárias bancárias dos pagamentos realizados através de DOC ou TED.';
COMMENT ON COLUMN branch.closing_payment_days IS 'Período de depósito – Indica o periodo de realização dos depósitos dos pagamentos a serem efetuados com as opções DAILY(Diário), WEEKLY(Semanal), BIWEEKLY(Quinzenal) e MONTHLY(Mensal).';
COMMENT ON COLUMN branch.minimum_deposit_value IS 'Valor mínimo de depósito – Define o valor mínimo para que um deposito de pagamento possa ser processado pela plataforma';
COMMENT ON COLUMN branch.invoice_receipt_period IS 'Tipo de Recebimento de NFe – indica a forma que a Filial irá enviar a Nota fiscal dos serviços prestados para recebimentos dos valores devidos com os seguintes valores previstos: XML – Recebe XML NF ou MANUAL – Recebimento manual NF;';
COMMENT ON COLUMN branch.invoice_receipt_type IS 'Período de Recebimento NFe – Indica a periodicidade em que a Filial irá enviar as NF dos serviços prestados, com as opções DAILY(Diário), WEEKLY(Semanal), BIWEEKLY(Quinzenal) e MONTHLY(Mensal).';

COMMENT ON COLUMN hirer.document_email IS 'E-mail de recebimento documentos quitação - Descrição do endereço de correio eletrônico ';

COMMENT ON COLUMN physical_person_detail.email IS 'E-mail da Pessoa Física';

COMMENT ON COLUMN hirer_branch.head_office_id IS 'Identificador do Contratante Matriz';
COMMENT ON COLUMN hirer_branch.document_email IS 'E-mail de recebimento documentos quitação - Descrição do endereço de correio eletrônico ';

COMMENT ON COLUMN contractor.rntrc IS 'Número RNTRC do motorista. Obrigatório quando o Contratado é pessoa jurídica';

COMMENT ON COLUMN person.cell_phone IS 'Número de Telefone Celular- Número do telefone Celular da pessoa';

COMMENT ON COLUMN product.code IS 'Código do Produto - Código de Identificação do Produto, campo chave de identificação do registro';
COMMENT ON COLUMN product.name IS 'Nome do Produto - Descrição do produto';
COMMENT ON COLUMN product.type IS 'Tipo de Produto - Tabela Interna com as opções FREIGHT("Frete") ou OTHERS("Outros")';
COMMENT ON COLUMN product.minimum_credit_insertion IS 'Valor mínimo que um crédito de contratante ou contratado deve ser aceito pela aplicação';
COMMENT ON COLUMN product.maximum_credit_insertion IS 'Valor máximo que um crédito de contratante ou contratado deve ser aceito pela aplicação';
COMMENT ON COLUMN product.payment_instrument_valid_days IS 'Dias de validade instrumento de Pagamento - Deve ser maior que zero';
COMMENT ON COLUMN product.situation IS 'Assume os valores ACTIVE("Ativo"), SUSPENDED("Suspenso") e CANCELED("Cancelado")';
COMMENT ON COLUMN product.membership_fee IS 'Valor da Taxa de Adesão - Maior ou igual a zero';
COMMENT ON COLUMN product.credit_insertion_fee IS 'Valor da Taxa de Inserção de Créditos - Maior ou igual a zero';
COMMENT ON COLUMN product.pay_inst_emission_fee IS 'Taxa de Emissão Instrumento de Pagamento - Maior ou igual a zero';
COMMENT ON COLUMN product.pay_inst_second_copy_fee IS 'Taxa de 2Via Instrumento de Pagamento - Maior ou igual a zero';
COMMENT ON COLUMN product.adm_credit_insert_fee IS 'Taxa Administração Inserção de Créditos - Valor percentual, maior ou igual a zero e menor que 1';

COMMENT ON COLUMN product_service_type.product_id IS 'Identificador do Produto';
COMMENT ON COLUMN product_service_type.service_type IS 'Tipo de Serviço – Qualifica o produto à permitir certo serviço, com as opções de FUEL_ALLOWANCE("Vale Abastecimento"),FREIGHT("Frete"), FREIGHT_RECEIPT("Quitaçao de Frete"), ELECTRONIC_TOLL("Pedágio eletrônico")';

COMMENT ON COLUMN contract.code IS 'Código do contrato gerado pela ROADCARD ou aplicação, ambos com ultimo dígito DV validado (ID-VIAGEM)';
COMMENT ON COLUMN contract.name IS 'Nome do Contrato - Descrição do contrato';
COMMENT ON COLUMN contract.payment_instrument_type IS 'Tipo de Instrumento de Pagamento. Permite os valores DIGITAL_WALLET("Carteira digital"), ACCOUNT_DEPOSIT("Conta deposito."), PREPAID_CARD("Cartao prepago"), VIRTUAL_CARD("Cartao virtual"), TAG("Tag")';
COMMENT ON COLUMN contract.begin_date IS 'Data Início do Contrato';
COMMENT ON COLUMN contract.end_date IS 'Data Término do Contrato';
COMMENT ON COLUMN contract.credit_insertion_type IS 'Tipo de Inserção de Crédito. Permite os valores BOLETO("Boleto"), DIRECT_DEBIT("Debito em conta."), CREDIT_CARD("Cartao de credito.") e PAMCARD_SYSTEM("Systema Pamcary")';
COMMENT ON COLUMN contract.issue_invoice IS 'emite NF - Indica se a nota fiscal do abastecimento será gerada pelo estabelecimento, com as opções 1- Emite NF; 0- Não Emite NF';
COMMENT ON COLUMN contract.document_number_invoice IS 'CNPJ Empresa emissão NF - Informa o CNPJ que deve ser gerada a NF de abastecimento pelo estabelecimento. Se vazio considera o CNPJ do Contratante senão considera este CNPJ que também deve estar cadastrado na tabela de Contratante.';
COMMENT ON COLUMN contract.situation IS 'Situação do Contrato. Permite os valores ACTIVE("Ativo"), SUSPENDED("Suspenso"),CANCELLED("Canclado"),FINALIZED("Finalizado") e EXPIRED("Expirado")';
COMMENT ON COLUMN contract.rntrc IS 'RNTRC - Número RNTRC do motorista.';
COMMENT ON COLUMN contract.origin IS 'Origem Cadastro do Contrato - Assume valore 1 -,PAMCARD - Gerado PAMCARD ou UNOPAY - Cadastrado na aplicação';

COMMENT ON COLUMN contract_service_type.contract_id IS 'Identificador do Contrato';
COMMENT ON COLUMN contract_service_type.service_type IS 'Tipo de Serviço – Qualifica o Contrato à permitir certo serviço, com as opções de FUEL_ALLOWANCE("Vale Abastecimento"),FREIGHT("Frete"), FREIGHT_RECEIPT("Quitaçao de Frete"), ELECTRONIC_TOLL("Pedágio eletrônico")';

COMMENT ON COLUMN payment_instrument.type IS 'Tipo de Instrumento de Pagamento. Permite os valores DIGITAL_WALLET("Carteira digital"), ACCOUNT_DEPOSIT("Conta deposito."), PREPAID_CARD("Cartao prepago"), VIRTUAL_CARD("Cartao virtual"), TAG("Tag")';
COMMENT ON COLUMN payment_instrument.payment_number IS 'Número do Instrumento de Pagamento - Numero de dezesseis posições é gerado pela nova plataforma pré-paga, com o ultimo digito DV modulo 11';
COMMENT ON COLUMN payment_instrument.created_date IS 'Data Cadastro Instrumento de Pagamento';
COMMENT ON COLUMN payment_instrument.expiration_date IS 'Data Validade Instrumento de Pagamento';
COMMENT ON COLUMN payment_instrument.password IS 'Senha do Instrumento de Pagamento';
COMMENT ON COLUMN payment_instrument.situation IS 'Situação Instrumentos de pagamento. Permite os valores ISSUED("Emitido"), ENABLED("Habilitado"), ACTIVE("Ativo"), SUSPENDED("Supspenso."), CANCELED("Cancelado")';
COMMENT ON COLUMN payment_instrument.external_number_id IS 'Numero Identificação Externo - Número que associa o Instrumento de pagamento a um campo de identificação de um sistema externo';

COMMENT ON COLUMN credit.credit_insertion_type IS 'Tipo de Inserção de Crédito. Permite os valores BOLETO("Boleto"), DIRECT_DEBIT("Debito em conta."), CREDIT_CARD("Cartao de credito.") e PAMCARD_SYSTEM("Systema Pamcary")';
COMMENT ON COLUMN credit.credit_number IS 'Numero identificador do crédito';
COMMENT ON COLUMN credit.created_date_time IS 'Data Hora da Inserção de Crédito';
COMMENT ON COLUMN credit.value IS 'Valor do Crédito';
COMMENT ON COLUMN credit.situation IS 'Situação do Crédito - Permite os valores PROCESSING("Processando"),TO_COLLECT("A cobrar"), CONFIRMED("Confirmado"), CANCELED("Cancelado"), EXPIRED("Expirado") e AVAILABLE("Disponivel")';
COMMENT ON COLUMN credit.credit_source IS 'Origem da Solicitação de Crédito,conforme Tabela 54 da Especificação funcional';
COMMENT ON COLUMN credit.cnab_id IS 'ID CNAB - Identificador do registro Padrão Febraban CNAB 400 ou CNAB 240 para receber (remessa) e enviar (retorno)valores para os bancos. ';
COMMENT ON COLUMN credit.available_value IS 'Saldo Disponível - Valor do credito disponível para uso';
COMMENT ON COLUMN credit.blocked_value IS 'Saldo bloqeuado - Valor do credito bloqueado para uso';

COMMENT ON COLUMN credit_payment_account.hirer_document IS 'Documento do Contratante';
COMMENT ON COLUMN credit_payment_account.service_type IS 'Tipo de Serviço – Qualifica à Conta de Pagamento permitir o crédito para certo serviço, com as opções de FUEL_ALLOWANCE("Vale Abastecimento"),FREIGHT("Frete"), FREIGHT_RECEIPT("Quitaçao de Frete"), ELECTRONIC_TOLL("Pedágio eletrônico")';
COMMENT ON COLUMN credit_payment_account.credit_insertion_type IS 'Tipo de Inserção de Crédito. Permite os valores BOLETO("Boleto"), DIRECT_DEBIT("Debito em conta."), CREDIT_CARD("Cartao de credito.") e PAMCARD_SYSTEM("Systema Pamcary")';
COMMENT ON COLUMN credit_payment_account.solicitation_date_time IS 'Data Hora da Solicitação de Crédito';
COMMENT ON COLUMN credit_payment_account.credit_number is 'Número do Crédito';
COMMENT ON COLUMN credit_payment_account.insertion_created_date_time IS 'Data e hora da inserção de créditos no sistema';
COMMENT ON COLUMN credit_payment_account.value IS 'Valor original do crédito';
COMMENT ON COLUMN credit_payment_account.situation IS 'Situação do Crédito - Permite os valores PROCESSING("Processando"),TO_COLLECT("A cobrar"), CONFIRMED("Confirmado"), CANCELED("Cancelado"), EXPIRED("Expirado") e AVAILABLE("Disponivel")';
COMMENT ON COLUMN credit_payment_account.credit_source IS 'Origem da Solicitação de Crédito';
COMMENT ON COLUMN credit_payment_account.available_balance IS 'Saldo disponível do crédito para uso';

COMMENT ON COLUMN contract_establishment.contract_id IS 'ID Contrato - Chave de identificação do Contrato';
COMMENT ON COLUMN contract_establishment.establishment_id IS 'ID Estabelecimento - Chave de identificação do estabelecimento';
COMMENT ON COLUMN contract_establishment.origin IS 'Origem Cadastro do Habilitação Estabelecimento - Assume valore 1 -,PAMCARD - Gerado PAMCARD ou UNOPAY - Cadastrado na aplicação';
COMMENT ON COLUMN contract_establishment.creation_date IS 'Data de Habilitação - Data que o estabelecimento foi habilitado ao contrato';

COMMENT ON COLUMN contractor_instrument_credit.payment_instrument_id IS 'Chave de identificação do Instrumento de Pagamento';
COMMENT ON COLUMN contractor_instrument_credit.contract_id IS 'Chave de identificação do Contrato';
COMMENT ON COLUMN contractor_instrument_credit.service_type IS 'Tipo de Serviço – Qualifica o crédito para certo serviço, com as opções de FUEL_ALLOWANCE("Vale Abastecimento"),FREIGHT("Frete"), FREIGHT_RECEIPT("Quitaçao de Frete"), ELECTRONIC_TOLL("Pedágio eletrônico")';
COMMENT ON COLUMN contractor_instrument_credit.credit_insertion_type IS 'Tipo de Inserção de Crédito. Permite os valores BOLETO("Boleto"), DIRECT_DEBIT("Debito em conta."), CREDIT_CARD("Cartao de credito.") e PAMCARD_SYSTEM("Systema Pamcary")';
COMMENT ON COLUMN contractor_instrument_credit.installment_number IS 'Número da parcela - Numero Sequencial único da parcela';
COMMENT ON COLUMN contractor_instrument_credit.value IS 'Valor do Crédito - Deve ser maior que zero';
COMMENT ON COLUMN contractor_instrument_credit.expiration_date_time IS 'Data de Validade do Crédito - Data deve ser maior ou igual a datado sistema';
COMMENT ON COLUMN contractor_instrument_credit.issuer_fee IS 'Valor Tarifa IP Emissora';
COMMENT ON COLUMN contractor_instrument_credit.situation IS 'Situação do Crédito - Permite os valores PROCESSING("Processando"),TO_COLLECT("A cobrar"), CONFIRMED("Confirmado"), CANCELED("Cancelado"), EXPIRED("Expirado") e AVAILABLE("Disponivel")';
COMMENT ON COLUMN contractor_instrument_credit.credit_payment_account_id IS 'ID da Transação Crédito Contratante Conta Pagamento - Identificador da Transação de crédito do contratante';
COMMENT ON COLUMN contractor_instrument_credit.available_balance IS 'Saldo Disponível - Calculado pela aplicação, na inserção de créditos assume mesmo valor do valor de credito';
COMMENT ON COLUMN contractor_instrument_credit.blocked_balance IS 'Saldo Bloqueado - Calculado pela aplicação, na inserção de créditos assume valor igual a zero.';
COMMENT ON COLUMN contractor_instrument_credit.created_date_time IS 'Data de Criação do Crédito';
COMMENT ON COLUMN contractor_instrument_credit.credit_type IS 'Tipo de Credito Valores permitidos: PAY_ADVANCE("Adiantamento"), FINAL_PAYMENT("Saldo Final")';

COMMENT ON COLUMN physical_person_detail.birth_date IS 'Data de Nascimento da Pessoa Física';

COMMENT ON COLUMN service_authorize.id IS 'ID Autorização - Chave de identificação do registro de Autorização de serviços';
COMMENT ON COLUMN service_authorize.authorization_number IS 'Número da Autorização - Numero único de 12 dígitos gerado pela plataforma';
COMMENT ON COLUMN service_authorize.authorization_date_time IS 'Data e hora da Autorização';
COMMENT ON COLUMN service_authorize.establishment_id IS 'ID Estabelecimento - Chave de identificação do estabelecimento';
COMMENT ON COLUMN service_authorize.contract_id IS 'ID Contrato - Chave de identificação do Contrato';
COMMENT ON COLUMN service_authorize.contractor_id IS 'ID Contratado - Chave de identificação do Contratado';
COMMENT ON COLUMN service_authorize.service_type IS 'Tipo de Serviço – FUEL_ALLOWANCE("Vale Abastecimento"),FREIGHT("Frete"), FREIGHT_RECEIPT("Quitaçao de Frete"), ELECTRONIC_TOLL("Pedágio eletrônico")';
COMMENT ON COLUMN service_authorize.event_id IS 'ID Evento - Chave de identificação do evento	';
COMMENT ON COLUMN service_authorize.event_quantity IS 'Quantidade de Evento';
COMMENT ON COLUMN service_authorize.event_value IS 'Valor do Evento';
COMMENT ON COLUMN service_authorize.value_fee IS 'Valor da Tarifa';
COMMENT ON COLUMN service_authorize.solicitation_date_time IS 'Data e hora da Solicitação';
COMMENT ON COLUMN service_authorize.credit_insertion_type IS 'Tipo de Inserção de Crédito. Permite os valores BOLETO("Boleto"), DIRECT_DEBIT("Debito em conta."), CREDIT_CARD("Cartao de credito.") e PAMCARD_SYSTEM("Systema Pamcary")';
COMMENT ON COLUMN service_authorize.contractor_inst_credit_id IS 'ID Credito Contrato Instrumento de Pagamento';
COMMENT ON COLUMN service_authorize.last_inst_credit_balance IS 'Saldo anterior do Contrato Instrumento de Pagamento';
COMMENT ON COLUMN service_authorize.current_inst_credit_balance IS 'Saldo atual do Contrato Instrumento de Pagamento';
COMMENT ON COLUMN service_authorize.cancellation_date_time IS 'Data de Cancelamento - Data e hora do cancelamento da autorização';
COMMENT ON COLUMN service_authorize.transaction_log_code IS 'Código da Mensagem de Erro - Conforme Tabela 12 - Mensagem de Erro';
COMMENT ON COLUMN service_authorize.transaction_log IS 'LOG da Transação - Descrição do LOG da transação';
COMMENT ON COLUMN service_authorize.user_id IS 'ID Usuário Transação - Identificado do usuário cadastrado no sistema';
COMMENT ON COLUMN service_authorize.situation IS 'Situação da Autorização - Permite os valores AUTHORIZED("Autorizada"),UNDETERMINED("Indeterminada"), AUTHORIZATION_ERROR("Erro na autorizaçao"), FINALIZED("Finalizada") e LOADING_ERROR("Erro na carga")';

COMMENT ON COLUMN product_payment_instrument_tp.product_id IS 'Identificador do Produto';
COMMENT ON COLUMN product_payment_instrument_tp.payment_instrument_type IS 'Tipo de Instrumento de Pagamento. Permite os valores DIGITAL_WALLET("Carteira digital"), ACCOUNT_DEPOSIT("Conta deposito."), PREPAID_CARD("Cartao prepago"), VIRTUAL_CARD("Cartao virtual"), TAG("Tag")';

COMMENT ON COLUMN product_credit_insertion_type.product_id IS 'Identificador do Produto';
COMMENT ON COLUMN product_credit_insertion_type.credit_insertion_type IS 'Tipo de Inserção de Crédito. Permite os valores BOLETO("Boleto"), DIRECT_DEBIT("Debito em conta."), CREDIT_CARD("Cartao de credito.") e PAMCARD_SYSTEM("Systema Pamcary")';

COMMENT ON COLUMN establishment_gathering.establishment_id IS 'Identificador do Produto';
COMMENT ON COLUMN establishment_gathering.gathering_channel IS 'Meio de captura – Tipo de equipamento onde será realizada a captura dos dados necessários para o processamento de uma autorização de serviço prestado pelo estabelecimento, com as opções WEB,MOBILE,POS';

COMMENT ON COLUMN cargo_contract.id IS 'ID Contrato Carga - Chave de identificação da Carga do Contrato	';
COMMENT ON COLUMN cargo_contract.partner_id IS 'ID Parceiro - Chave de identificação do Parceiro';
COMMENT ON COLUMN cargo_contract.contract_id IS 'ID Contrato - Chave de identificação do Contrato';
COMMENT ON COLUMN cargo_contract.caveat IS 'Ressalva do Documento. Permite Valores  S("Sim"),N("Nao")';
COMMENT ON COLUMN cargo_contract.cargo_profile IS 'Perfil da Carga. Permite Valores DRY_CARGO("Carga seca"), IN_BULK("A granel");';
COMMENT ON COLUMN cargo_contract.receipt_observation IS 'Observação Quitação';
COMMENT ON COLUMN cargo_contract.receipt_step IS 'Etapa de Quitação - Permite Valores CONSULTED("Consultado"), COLLECTED("Coletado"), RECEIVED("Recebido"), DIGITIZED("Digitalizado"), ARCHIVED("Arquivado"), SENT("Enviado")';
COMMENT ON COLUMN cargo_contract.payment_source IS 'Fonte do Pagamento. Permite os valores HIRER("Contratante"), ESTABLISHMENT("Estabelecimento") e PAMCARY("Pamcary")';
COMMENT ON COLUMN cargo_contract.travel_situation IS 'Situação da viagem. Permite os valores PENDING("PENDENTE"), OPENED("EM ABERTO"), FINISHED("FINALIZADA"), CANCELED("CANCELADA") e ACTIVE("ATIVA")';
COMMENT ON COLUMN cargo_contract.created_date_time IS 'Data de Criação';
COMMENT ON COLUMN cargo_contract.receipt_situation IS 'Situação da Quitação. Permite os valores ACCEPTED("Aceita") e REFUSED("Recusada")';
COMMENT ON COLUMN cargo_contract.reason_receipt_situation IS 'Motivo da Situação da Quitação. Permite os valores. DOCUMENTATION_OK("Documentacao Ok"), INCOMPLETE_DOCUMENTATION("Documentacao incompleta"), CAVEAT_DOCUMENTATION("Documentacao com ressalva.") e GAVE_UP("Desistencia")';

COMMENT ON COLUMN complementary_travel_document.id IS 'ID Documento Complementar - Chave de identificação do Documento Complementar no Contrato';
COMMENT ON COLUMN complementary_travel_document.quantity IS 'Quantidade de Documentos - Quantidade de Documentos';
COMMENT ON COLUMN complementary_travel_document.type IS 'Tipo de Documento Complementar - Permite os valores PAL("Recibo de Entrega de Pallet"), PFI("Passe Fiscal"), IBA("Licença IBAMA"), CTE("CT-e Assinado"), NFC("Canhoto NF Cliente") e CTB("CT-b")';
COMMENT ON COLUMN complementary_travel_document.document_number IS 'Número do Documento Complementar- Número de identificação do documento';
COMMENT ON COLUMN complementary_travel_document.situation IS 'Situação do Documento Complementar. Permite os valores DIGITIZED("Digitalizado"), RETIRED("Retirado")';
COMMENT ON COLUMN complementary_travel_document.caveat IS 'Ressalva do Documento Complementar - assume valores S -Sim ou N - Não ';
COMMENT ON COLUMN complementary_travel_document.created_date_time IS 'Data cadastro do Documento Complementar';
COMMENT ON COLUMN complementary_travel_document.delivery_date_time IS 'Data de entrega do Documento';
COMMENT ON COLUMN complementary_travel_document.cargo_contract_id IS 'ID Contrato Carga - Chave de identificação da Carga do Contrato';

COMMENT ON COLUMN travel_document.id IS 'ID Documento  - Chave de identificação do Documento Complementar no Contrato';
COMMENT ON COLUMN travel_document.quantity IS 'Quantidade de Documentos - Quantidade de Documentos';
COMMENT ON COLUMN travel_document.type IS 'Tipo de Documento  - Conforme Tabela 22 da especificação funcional';
COMMENT ON COLUMN travel_document.document_number IS 'Número do Documento - Número de identificação do documento';
COMMENT ON COLUMN travel_document.situation IS 'Situação do Documento . Permite os valores DIGITIZED("Digitalizado"), RETIRED("Retirado")';
COMMENT ON COLUMN travel_document.caveat IS 'Ressalva do Documento  - assume valores S -Sim ou N - Não ';
COMMENT ON COLUMN travel_document.created_date_time IS 'Data cadastro do Documento ';
COMMENT ON COLUMN travel_document.delivery_date_time IS 'Data de entrega do Documento';
COMMENT ON COLUMN travel_document.cargo_contract_id IS 'ID Contrato Carga - Chave de identificação da Carga do Contrato';
COMMENT ON COLUMN travel_document.contract_id IS 'ID Contrato - Chave de identificação do Contrato';
COMMENT ON COLUMN travel_document.cargo_weight IS 'Peso da Carga';
COMMENT ON COLUMN travel_document.damaged_items IS 'Itens Avariados';