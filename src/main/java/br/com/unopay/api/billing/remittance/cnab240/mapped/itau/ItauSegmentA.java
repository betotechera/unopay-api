package br.com.unopay.api.billing.remittance.cnab240.mapped.itau;

import br.com.unopay.api.billing.remittance.cnab240.filler.FilledRecord;
import br.com.unopay.api.billing.remittance.model.ItauAccountField;
import br.com.unopay.api.billing.remittance.model.PaymentRemittanceItem;
import br.com.unopay.api.billing.remittance.model.RemittancePayee;
import br.com.unopay.api.util.Rounder;

import static br.com.unopay.api.billing.remittance.cnab240.filler.ItauRemittanceLayout.getBatchSegmentA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.AGENCIA_CONTA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.AVISO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BANCO_COMPENSACAO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BANCO_FAVORECIDO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BRANCOS_1;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BRANCOS_2;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BRANCOS_3;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CAMARA_CENTRALIZADORA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CODIGO_ISPB;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DATA_PAGAMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DATA_REAL_PAGAMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.FIM_FEBRABAN;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.FINALIDADE_DETALHE;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.FINALIDADE_DOC;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.FINALIDADE_TED;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.LOTE_SERVICO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NOME_FAVORECIDO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NOSSO_NUMERO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO_DOCUMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO_INSCRICAO_FAVORECIDO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO_REGISTRO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.OCORRENCIAS;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.SEGMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.SEU_NUMERO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_MOEDA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_MOVIMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_REGISTRO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.VALOR_PAGAMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.VALOR_REAL_PAGAMENTO;

public class ItauSegmentA {

    public FilledRecord create(final PaymentRemittanceItem remittanceItem, Integer position) {
        RemittancePayee payee = remittanceItem.getPayee();
        return new FilledRecord(getBatchSegmentA()).
                defaultFill(BANCO_COMPENSACAO).
                fill(LOTE_SERVICO, position).
                defaultFill(TIPO_REGISTRO).
                fill(NUMERO_REGISTRO, position).
                defaultFill(SEGMENTO).
                defaultFill(TIPO_MOVIMENTO).
                defaultFill(CAMARA_CENTRALIZADORA).
                fill(BANCO_FAVORECIDO, payee.getBankCode()).
                fill(AGENCIA_CONTA, new ItauAccountField(payee).get()).
                fill(NOME_FAVORECIDO, payee.getName()).
                defaultFill(SEU_NUMERO).
                defaultFill(DATA_PAGAMENTO).
                defaultFill(TIPO_MOEDA).
                defaultFill(CODIGO_ISPB).
                defaultFill(BRANCOS_1).
                fill(VALOR_PAGAMENTO, Rounder.roundToString(remittanceItem.getValue())).
                defaultFill(NOSSO_NUMERO).
                defaultFill(BRANCOS_2).
                defaultFill(DATA_REAL_PAGAMENTO).
                defaultFill(VALOR_REAL_PAGAMENTO).
                defaultFill(FINALIDADE_DETALHE).
                defaultFill(BRANCOS_3).
                defaultFill(NUMERO_DOCUMENTO).
                fill(NUMERO_INSCRICAO_FAVORECIDO, payee.getDocumentNumber()).
                defaultFill(FINALIDADE_DOC).
                defaultFill(FINALIDADE_TED).
                defaultFill(FIM_FEBRABAN).
                defaultFill(AVISO).
                defaultFill(OCORRENCIAS);
    }
}
