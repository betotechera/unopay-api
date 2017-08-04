package br.com.unopay.api.payment.cnab240.mapped;

import br.com.unopay.api.payment.cnab240.filler.FilledRecord;
import br.com.unopay.api.payment.model.PaymentRemittance;
import br.com.unopay.api.payment.model.RemittancePayer;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.ObjectUtils;

import static br.com.unopay.api.payment.cnab240.Cnab240Generator.DATE_FORMAT;
import static br.com.unopay.api.payment.cnab240.Cnab240Generator.HOUR_FORMAT;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayout.getRemittanceHeader;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.AGENCIA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.BANCO_COMPENSACAO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.CODIGO_REMESSA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.CONVEIO_BANCO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DATA_GERACAO_ARQUIVO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DENSIDADE_GRAVACAO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DIGITO_AGENCIA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DIGITO_AGENCIA_CONTA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DIGITO_CONTA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.FIM_FEBRABAN;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.HORA_GERACAO_ARQUIVO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.INICIO_FEBRABAN;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.LAYOUT_ARQUIVO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.LOTE_SERVICO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.MEIO_FEBRABAN;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NOME_BANCO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NOME_EMPRESA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NUMERO_CONTA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NUMERO_INSCRICAO_EMPRESA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.RESERVADO_BANCO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.RESERVADO_EMPRESA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.SEQUENCIAL_ARQUIVO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.TIPO_INSCRICAO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.TIPO_REGISTRO;

public class RemittanceHeader {

    private Date currentDate;

    public RemittanceHeader(){}

    public RemittanceHeader(Date currentDate){
        this.currentDate = ObjectUtils.clone(currentDate);
    }

    public FilledRecord create(final PaymentRemittance remittance) {
        RemittancePayer payer = remittance.getPayer();
        return new FilledRecord(getRemittanceHeader()).
            defaultFill(BANCO_COMPENSACAO).
            defaultFill(LOTE_SERVICO).
            defaultFill(TIPO_REGISTRO).
            defaultFill(INICIO_FEBRABAN).
            defaultFill(TIPO_INSCRICAO).
            fill(NUMERO_INSCRICAO_EMPRESA, payer.getDocumentNumber()).
            fill(CONVEIO_BANCO, payer.getBankAgreementNumberForCredit()).
            fill(AGENCIA, payer.getAgency()).
            fill(DIGITO_AGENCIA, payer.agentDvFirstDigit()).
            fill(NUMERO_CONTA, payer.getAccountNumber()).
            fill(DIGITO_CONTA, payer.accountDvFirstDigit()).
            fill(DIGITO_AGENCIA_CONTA, payer.accountDvLastDigit()).
            fill(NOME_EMPRESA, payer.getName()).
            fill(NOME_BANCO, payer.getBankName()).
            defaultFill(MEIO_FEBRABAN).
            defaultFill(CODIGO_REMESSA).
            fill(DATA_GERACAO_ARQUIVO, new SimpleDateFormat(DATE_FORMAT).format(currentDate)).
            fill(HORA_GERACAO_ARQUIVO, new SimpleDateFormat(HOUR_FORMAT).format(currentDate)).
            fill(SEQUENCIAL_ARQUIVO, remittance.getNumber()).
            defaultFill(LAYOUT_ARQUIVO).
            defaultFill(DENSIDADE_GRAVACAO).
            defaultFill(RESERVADO_BANCO).
            defaultFill(RESERVADO_EMPRESA).
            defaultFill(FIM_FEBRABAN);
    }
}
