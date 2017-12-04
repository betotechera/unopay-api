COMMENT ON TABLE bank IS 'Bancos cadastrados no Banco Central';
COMMENT ON TABLE OAUTH_GROUP_AUTHORITIES IS 'Relacionamento do Perfil de Acesso com permissões';
COMMENT ON TABLE OAUTH_GROUP_MEMBERS IS 'Relacionamento do Perfil de Acesso com Usuários';
COMMENT ON TABLE OAUTH_CLIENT_DETAILS IS  'Clientes do Sistema - Clientes que acessam a plataforma ,sem precisar estarem logados';
COMMENT ON TABLE OAUTH_ACCESS_TOKEN IS 'Tabela de Uso Interno da Funcionalidade OAuth2 para controle dos access_token gerados pelo sistema';
COMMENT ON TABLE OAUTH_APPROVALS IS 'Tabela de Uso Interno da Funcionalidade OAuth2 para as permissões concedidas pelo sistema';
COMMENT ON TABLE OAUTH_CLIENT_TOKEN IS 'Tabela de Uso Interno da Funcionalidade OAuth2 para controle dos access_token gerados para clientes';
COMMENT ON TABLE OAUTH_CODE IS 'Tabela de Uso Interno da Funcionalidade OAuth2 para as permissões do sistema';

COMMENT ON COLUMN bank.bacen_code IS 'Código do Banco - Número de identificação do banco, definido pelo Banco Central ';
COMMENT ON COLUMN bank.name IS 'Nome do Banco cadastrado no Banco Central';

COMMENT ON COLUMN OAUTH_GROUP_AUTHORITIES.group_id IS 'Identificador do Perfil de Acesso';
COMMENT ON COLUMN OAUTH_GROUP_AUTHORITIES.authority IS 'Nome da Permissão';

COMMENT ON COLUMN OAUTH_GROUP_MEMBERS.group_id IS 'Identificador do Perfil de Acesso';
COMMENT ON COLUMN OAUTH_GROUP_MEMBERS.user_id IS 'Identificador do Usuário';

COMMENT ON COLUMN OAUTH_CLIENT_DETAILS.client_id IS 'Identificador do Cleinte';
COMMENT ON COLUMN OAUTH_CLIENT_DETAILS.resource_ids IS 'Recursos do sistema que o cliente poderá acessar';
COMMENT ON COLUMN OAUTH_CLIENT_DETAILS.client_secret IS 'Senha criptografada do cliente';
COMMENT ON COLUMN OAUTH_CLIENT_DETAILS.scope IS 'Escopo do acesso do cliente';
COMMENT ON COLUMN OAUTH_CLIENT_DETAILS.authorized_grant_types IS 'Tipos de grant_type permitidos pra acesso do cliente';
COMMENT ON COLUMN OAUTH_CLIENT_DETAILS.web_server_redirect_uri IS 'redirec_uri retornada para o cliente após o token gerado';
COMMENT ON COLUMN OAUTH_CLIENT_DETAILS.access_token_validity IS 'Segundos que o access_token gerado será valido';
COMMENT ON COLUMN OAUTH_CLIENT_DETAILS.refresh_token_validity IS 'Segundos que o refresh_token gerado será valido';
COMMENT ON COLUMN OAUTH_CLIENT_DETAILS.additional_information IS 'Informações adicionais do cliente';
COMMENT ON COLUMN OAUTH_CLIENT_DETAILS.autoapprove IS 'Flag se usuário deve ser auto aprovado';

COMMENT ON COLUMN CREDIT.HIRER_DOCUMENT IS 'Numero de documento do Contratante (CPF ou CNPJ)';
COMMENT ON COLUMN CREDIT.SERVICE_TYPE IS 'Tipo de Serviço do crédtio, com as opções de FUEL_ALLOWANCE("Vale Abastecimento"),FREIGHT("Frete"), FREIGHT_RECEIPT("Quitação de Frete"), ELECTRONIC_TOLL("Pedágio eletrônico")';
COMMENT ON COLUMN CREDIT.CNAB_ID is 'Identificador do documento CNAB gerado';

COMMENT ON COLUMN CREDIT_PAYMENT_ACCOUNT.PAYMENT_ACCOUNT_ID IS 'Identificador da conta pagamento';

COMMENT ON COLUMN ESTABLISHMENT.MOVEMENT_ACCOUNT_ID IS 'Identificador da Conta Movimento';

COMMENT ON COLUMN ISSUER.PAYMENT_ACCOUNT_ID IS 'Identificador da Conta Pagamento';
COMMENT ON COLUMN ISSUER.MOVEMENT_ACCOUNT_ID IS 'Identificador da Conta Movimento';