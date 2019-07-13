package br.com.unopay.api.billing.remittance.cnab240.mapped.itau;

import br.com.unopay.api.billing.remittance.cnab240.filler.FilledRecord;
import br.com.unopay.api.billing.remittance.model.PaymentRemittance;
import br.com.unopay.api.billing.remittance.model.RemittancePayer;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.ObjectUtils;

import static br.com.unopay.api.billing.remittance.cnab240.ItauCnab240Generator.DATE_FORMAT;
import static br.com.unopay.api.billing.remittance.cnab240.ItauCnab240Generator.HOUR_FORMAT;
import static br.com.unopay.api.billing.remittance.cnab240.filler.ItauRemittanceLayout.getRemittanceHeader;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.AGENCIA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BANCO_COMPENSACAO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BRANCOS_1;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BRANCOS_2;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BRANCOS_3;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BRANCOS_5;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BRANCOS_6;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BRANCO_4;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CODIGO_REMESSA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DATA_GERACAO_ARQUIVO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DENSIDADE_GRAVACAO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DIGITO_AGENCIA_CONTA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DIGITO_CONTA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.HORA_GERACAO_ARQUIVO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.LAYOUT_ARQUIVO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.LOTE_SERVICO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NOME_BANCO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NOME_EMPRESA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO_CONTA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO_INSCRICAO_EMPRESA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.RESERVADO_BANCO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_INSCRICAO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_REGISTRO;

public class ItauRemittanceHeader {

    private Date currentDate;

    public ItauRemittanceHeader(){}

    public ItauRemittanceHeader(Date currentDate){
        this.currentDate = ObjectUtils.clone(currentDate);
    }

    public FilledRecord create(final PaymentRemittance remittance) {
        RemittancePayer payer = remittance.getPayer();
        return new FilledRecord(getRemittanceHeader()).
                defaultFill(BANCO_COMPENSACAO).
                defaultFill(LOTE_SERVICO).
                defaultFill(TIPO_REGISTRO).
                defaultFill(BRANCOS_1).
                defaultFill(LAYOUT_ARQUIVO).
                defaultFill(TIPO_INSCRICAO).
                fill(NUMERO_INSCRICAO_EMPRESA, payer.getDocumentNumber()).
                defaultFill(BRANCOS_2).
                fill(AGENCIA, payer.getAgency()).
                defaultFill(BRANCOS_3).
                fill(NUMERO_CONTA, payer.getAccountNumber()).
                defaultFill(BRANCO_4).
                fill(DIGITO_AGENCIA_CONTA, payer.accountDvLastDigit()).
                fill(NOME_EMPRESA, payer.getName()).
                fill(NOME_BANCO, payer.getBankName()).
                defaultFill(BRANCOS_5).
                defaultFill(CODIGO_REMESSA).
                fill(DATA_GERACAO_ARQUIVO, new SimpleDateFormat(DATE_FORMAT).format(currentDate)).
                fill(HORA_GERACAO_ARQUIVO, new SimpleDateFormat(HOUR_FORMAT).format(currentDate)).
                defaultFill(BRANCOS_6).
                defaultFill(DENSIDADE_GRAVACAO).
                defaultFill(RESERVADO_BANCO);
    }
}
