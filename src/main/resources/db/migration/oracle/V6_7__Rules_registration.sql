alter table product drop column maximum_credit_insertion;
alter table product drop column minimum_credit_insertion;
alter table payment_rule_group add maximum_credit_insertion decimal(*,2);
alter table payment_rule_group add minimum_credit_insertion decimal(*,2);
alter table credit add issuer_document varchar(20) default '24122925000173' not null;
alter table legal_person_detail add municipal_inscription_number varchar(150) null;
alter table issuer add financier_mail_for_remittance varchar(256) default 'financeiro@roadcard.com.br' not null;
alter table payment_bank_account add bank_agreement_number_for_debit varchar(20) default '0000000000' not null;
alter table remittance_payer add bank_agreement_number_for_debit varchar(20) default '0000000000' not null;

COMMENT ON COLUMN payment_rule_group.maximum_credit_insertion IS 'valor maximo de credito para os produtos do arranjo de pagamento';
COMMENT ON COLUMN payment_rule_group.minimum_credit_insertion IS 'valor minimo de credito para os produtos do arranjo de pagamento';
COMMENT ON COLUMN credit.issuer_document IS 'documento do emissor de destino do credito';
COMMENT ON COLUMN legal_person_detail.municipal_inscription_number IS 'inscricao municipal';
COMMENT ON COLUMN legal_person_detail.state_inscription_number IS 'inscricao estadual';
COMMENT ON COLUMN payment_bank_account.bank_agreement_number_for_credit IS 'codigo de convenio com o banco para credito';
COMMENT ON COLUMN payment_bank_account.bank_agreement_number_for_debit IS 'codigo de convenio com o banco para debito';
COMMENT ON COLUMN remittance_payer.bank_agreement_number_for_debit IS 'codigo de convenio com o banco para debito';
COMMENT ON COLUMN issuer.financier_mail_for_remittance IS 'email financeiro para receber as notificaçoes de fechamento de remessa';

