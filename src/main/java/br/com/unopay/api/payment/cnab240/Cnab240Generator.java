package br.com.unopay.api.payment.cnab240;

import br.com.unopay.api.bacen.model.BankAccount;
import br.com.unopay.api.model.BatchClosing;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.payment.cnab240.FilledRecord;
import java.text.SimpleDateFormat;
import java.util.Date;

import static br.com.unopay.api.payment.cnab240.RemittanceLayout.AGENCIA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.CODIGO_BANCO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.CODIGO_REMESSA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.CONVEIO_BANCO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.DATA_GERACAO_ARQUIVO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.DENSIDADE_GRAVACAO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.DIGITO_AGENCIA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.DIGITO_AGENCIA_CONTA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.DIGITO_CONTA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.FIM_FEBRABAN;
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.HORA_GERACAO_ARQUIVO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.INICIO_FEBRABAN;
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.LAYOUT_ARQUIVO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.LOTE_SERVICO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.MEIO_FEBRABAN;
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.NOME_BANCO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.NOME_EMPRESA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.NUMERO_CONTA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.NUMERO_INSCRICAO_EMPRESA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.RESERVADO_BANCO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.RESERVADO_EMPRESA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.SEQUENCIAL_ARQUIVO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.TIPO_INSCRICAO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.TIPO_REGISTRO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.getRemittanceHeader;

public class Cnab240Generator {

    private Date currentDate;

    public Cnab240Generator(Date currentDate){
        this.currentDate = currentDate;
    }

    public String generate(BatchClosing batchClosing) {
        BankAccount bankAccount = batchClosing.getIssuer().getPaymentAccount().getBankAccount();
        Person person = batchClosing.getIssuer().getPerson();
        FilledRecord record = new FilledRecord(getRemittanceHeader()) {{
            fill(CODIGO_BANCO, bankAccount.getBacenCode());
            defaultFill(LOTE_SERVICO);
            defaultFill(TIPO_REGISTRO);
            defaultFill(INICIO_FEBRABAN);
            defaultFill(TIPO_INSCRICAO);
            fill(NUMERO_INSCRICAO_EMPRESA, person.getDocument().getNumber());
            fill(CONVEIO_BANCO, batchClosing.getIssuer().getPaymentAccount().getBankAgreementNumber());
            fill(AGENCIA, bankAccount.getAgency());
            fill(DIGITO_AGENCIA, bankAccount.agentDvFirstDigit());
            fill(NUMERO_CONTA, bankAccount.getAccountNumber());
            fill(DIGITO_CONTA, bankAccount.accountDvFirstDigit());
            fill(DIGITO_AGENCIA_CONTA, bankAccount.accountDvLastDigit());
            fill(NOME_EMPRESA, person.getName());
            fill(NOME_BANCO, bankAccount.getBank().getName());
            defaultFill(MEIO_FEBRABAN);
            defaultFill(CODIGO_REMESSA);
            fill(DATA_GERACAO_ARQUIVO, new SimpleDateFormat("ddMMyyyy").format(currentDate));
            fill(HORA_GERACAO_ARQUIVO, new SimpleDateFormat("hhmmss").format(currentDate));
            fill(SEQUENCIAL_ARQUIVO, "001");
            defaultFill(LAYOUT_ARQUIVO);
            defaultFill(DENSIDADE_GRAVACAO);
            defaultFill(RESERVADO_BANCO);
            defaultFill(RESERVADO_EMPRESA);
            defaultFill(FIM_FEBRABAN);
        }};
        return record.build();
    }
}
