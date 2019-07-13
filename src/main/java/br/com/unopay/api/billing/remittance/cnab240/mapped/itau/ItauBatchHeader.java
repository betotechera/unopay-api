package br.com.unopay.api.billing.remittance.cnab240.mapped.itau;

import br.com.unopay.api.billing.remittance.cnab240.filler.FilledRecord;
import br.com.unopay.api.billing.remittance.model.PaymentRemittance;
import br.com.unopay.api.billing.remittance.model.PaymentRemittanceItem;
import br.com.unopay.api.billing.remittance.model.RemittancePayer;

import static br.com.unopay.api.billing.remittance.cnab240.filler.ItauRemittanceLayout.getBatchHeader;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.AGENCIA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BANCO_COMPENSACAO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BRANCOS_1;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BRANCOS_2;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BRANCOS_3;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CEP;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CIDADE;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.COMPLEMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DIGITO_AGENCIA_CONTA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DIGITO_CONTA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.ESTADO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.FIM_FEBRABAN;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.FINALIDADE_COD;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.FORMA_LANCAMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.HISTORICO_CC;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.IDENTIFICACAO_LANCAMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.INICIO_FEBRABAN;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.LAYOUT_ARQUIVO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.LOGRADOURO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.LOTE_SERVICO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NOME_EMPRESA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO_CONTA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO_INSCRICAO_EMPRESA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.OCORRENCIAS;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_INSCRICAO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_OPERACAO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_REGISTRO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_SERVICO;

public class ItauBatchHeader {

    public ItauBatchHeader(){}

    public FilledRecord create(final PaymentRemittance remittance, Integer position) {
        RemittancePayer payer = remittance.getPayer();
        return new FilledRecord(getBatchHeader()).
                defaultFill(BANCO_COMPENSACAO).
                fill(LOTE_SERVICO, position).
                defaultFill(TIPO_REGISTRO).
                defaultFill(TIPO_OPERACAO).
                defaultFill(TIPO_SERVICO).
                defaultFill(FORMA_LANCAMENTO).
                defaultFill(LAYOUT_ARQUIVO).
                defaultFill(INICIO_FEBRABAN).
                defaultFill(TIPO_INSCRICAO).
                fill(NUMERO_INSCRICAO_EMPRESA, payer.getDocumentNumber()).
                defaultFill(IDENTIFICACAO_LANCAMENTO).
                defaultFill(BRANCOS_1).
                fill(AGENCIA, payer.getAgency()).
                defaultFill(BRANCOS_2).
                fill(NUMERO_CONTA, payer.getAccountNumber()).
                defaultFill(BRANCOS_3).
                fill(DIGITO_AGENCIA_CONTA, payer.accountDvLastDigit()).
                fill(NOME_EMPRESA, payer.getName()).
                defaultFill(FINALIDADE_COD).
                defaultFill(HISTORICO_CC).
                fill(LOGRADOURO, payer.getStreetName()).
                fill(NUMERO, payer.getNumber()).
                fill(COMPLEMENTO, payer.getComplement()).
                fill(CIDADE, payer.getCity()).
                fill(CEP, payer.getZipCode()).
                fill(ESTADO, payer.getState().name()).
                defaultFill(FIM_FEBRABAN).
                defaultFill(OCORRENCIAS);
    }
}
