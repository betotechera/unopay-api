package br.com.unopay.api.billing.remittance.cnab240.mapped;

import br.com.unopay.api.billing.remittance.cnab240.filler.FilledRecord;
import br.com.unopay.api.billing.remittance.model.PaymentRemittanceItem;
import br.com.unopay.api.billing.remittance.model.RemittancePayee;
import br.com.unopay.api.billing.remittance.model.RemittancePayer;
import br.com.unopay.api.util.Rounder;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.ObjectUtils;

import static br.com.unopay.api.billing.remittance.cnab240.Cnab240Generator.DATE_FORMAT;
import static br.com.unopay.api.billing.remittance.cnab240.filler.BradescoRemittanceLayout.getBatchSegmentA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.AGENCIA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.AVISO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BANCO_COMPENSACAO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BANCO_FAVORECIDO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CAMARA_CENTRALIZADORA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DATA_PAGAMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DATA_REAL_PAGAMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DIGITO_AGENCIA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DIGITO_AGENCIA_CONTA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DIGITO_CONTA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DOCUMENTO_ATRIBUIDO_BANCO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DOCUMENTO_ATRIBUIDO_EMPRESA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.FIM_FEBRABAN;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.FINALIDADE_DOC;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.FINALIDADE_TED;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.INFORMACAO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.INSTITUICAO_MOVIMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.LOTE_SERVICO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NOME_FAVORECIDO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO_CONTA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO_REGISTRO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.OCORRENCIAS;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_MOEDA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.SEGMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_MOEDA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_MOVIMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_REGISTRO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.VALOR_PAGAMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.VALOR_REAL_PAGAMENTO;

public class SegmentA {

    private Date currentDate;

    public SegmentA(){}

    public SegmentA(Date currentDate){
        this.currentDate = ObjectUtils.clone(currentDate);
    }

    public FilledRecord create(final PaymentRemittanceItem remittanceItem, Integer position) {
        RemittancePayee payee = remittanceItem.getPayee();
        RemittancePayer payer = remittanceItem.getPaymentRemittance().getPayer();
        return new FilledRecord(getBatchSegmentA()).
            fill(BANCO_COMPENSACAO, payer.getBankCode()).
            fill(LOTE_SERVICO, position).
            defaultFill(TIPO_REGISTRO).
            fill(NUMERO_REGISTRO, position).
            defaultFill(SEGMENTO).
            defaultFill(TIPO_MOVIMENTO).
            defaultFill(INSTITUICAO_MOVIMENTO).
            defaultFill(CAMARA_CENTRALIZADORA).
            fill(BANCO_FAVORECIDO, payee.getBankCode()).
            fill(AGENCIA, payee.agentDvFirstDigit()).
            fill(DIGITO_AGENCIA, payee.agentDvLastDigit()).
            fill(NUMERO_CONTA, payee.getAccountNumber()).
            fill(DIGITO_CONTA, payee.accountDvFirstDigit()).
            fill(DIGITO_AGENCIA_CONTA, payee.accountDvLastDigit()).
            fill(NOME_FAVORECIDO, payee.getName()).
            fill(DOCUMENTO_ATRIBUIDO_EMPRESA, payee.getDocumentNumber()).
            fill(DATA_PAGAMENTO, new SimpleDateFormat(DATE_FORMAT).format(currentDate)).
            defaultFill(TIPO_MOEDA).
            defaultFill(QUANTIDADE_MOEDA).
            fill(VALOR_PAGAMENTO, Rounder.roundToString(remittanceItem.getValue())).
            defaultFill(DOCUMENTO_ATRIBUIDO_BANCO).
            defaultFill(DATA_REAL_PAGAMENTO).
            fill(VALOR_REAL_PAGAMENTO, Rounder.roundToString(remittanceItem.getValue())).
            defaultFill(INFORMACAO).
            defaultFill(FINALIDADE_DOC).
            defaultFill(FINALIDADE_TED).
            defaultFill(FIM_FEBRABAN).
            defaultFill(AVISO).
            defaultFill(OCORRENCIAS);
    }
}
