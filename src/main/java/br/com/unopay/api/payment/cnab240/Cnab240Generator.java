package br.com.unopay.api.payment.cnab240;

import br.com.unopay.api.bacen.model.BankAccount;
import br.com.unopay.api.model.Address;
import br.com.unopay.api.model.BatchClosing;
import br.com.unopay.api.model.Person;
import java.text.SimpleDateFormat;
import java.util.Date;

import static br.com.unopay.api.payment.cnab240.RemittanceLayout.getBatchHeader;
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.getBatchTrailer;
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.getRemittanceHeader;
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.getRemittanceTrailer;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.AGENCIA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.CEP;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.CIDADE;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.CODIGO_BANCO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.CODIGO_REMESSA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.COMPLEMENTO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.COMPLEMENTO_CEP;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.CONVEIO_BANCO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DATA_GERACAO_ARQUIVO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DENSIDADE_GRAVACAO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DIGITO_AGENCIA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DIGITO_AGENCIA_CONTA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DIGITO_CONTA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.ESTADO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.FIM_FEBRABAN;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.FORMA_LANCAMENTO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.HORA_GERACAO_ARQUIVO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.INICIO_FEBRABAN;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.LAYOUT_ARQUIVO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.LOGRADOURO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.LOTE_SERVICO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.MEIO_FEBRABAN;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.MENSAGEM;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NOME_BANCO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NOME_EMPRESA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NUMERO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NUMERO_AVISO_DEBITO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NUMERO_CONTA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NUMERO_INSCRICAO_EMPRESA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.OCORRENCIAS;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.QUANTIDADE_CONTAS;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.QUANTIDADE_LOTES;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.QUANTIDADE_MOEDAS;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.QUANTIDADE_REGISTROS;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.RESERVADO_BANCO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.RESERVADO_EMPRESA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.SEQUENCIAL_ARQUIVO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.SOMATORIA_VALORES;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.TIPO_INSCRICAO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.TIPO_OPERACAO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.TIPO_REGISTRO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.TIPO_SERVICO;

public class Cnab240Generator {

    private Date currentDate;

    public Cnab240Generator(Date currentDate){
        this.currentDate = currentDate;
    }

    public String generate(BatchClosing batchClosing) {
        BankAccount bankAccount = batchClosing.getIssuer().getPaymentAccount().getBankAccount();
        Person person = batchClosing.getIssuer().getPerson();
        Address address = person.getAddress();
        FilledRecord remittanceHeader = createRemittanceHeader(batchClosing, bankAccount, person);
        FilledRecord batchHeader = createBatchHeader(batchClosing, bankAccount, person, address);
        FilledRecord batchTrailer = createBatchTrailer(batchClosing, bankAccount);
        FilledRecord remittanceTrailer = createRemittanceTrailer(bankAccount);
        return new WrappedRecord()
                .createHeader(remittanceHeader)
                .addRecord(batchHeader)
                .addRecord(batchTrailer)
                .createTrailer(remittanceTrailer)
                .build();
    }

    private FilledRecord createBatchTrailer(final BatchClosing batchClosing, final BankAccount bankAccount) {
        return new FilledRecord(getBatchTrailer()) {{
                fill(CODIGO_BANCO, bankAccount.getBacenCode());
                fill(LOTE_SERVICO, "0009");
                defaultFill(TIPO_REGISTRO);
                defaultFill(INICIO_FEBRABAN);
                fill(SOMATORIA_VALORES,"1");
                fill(QUANTIDADE_MOEDAS, batchClosing.getValue().toString());
                defaultFill(NUMERO_AVISO_DEBITO);
                defaultFill(FIM_FEBRABAN);
                defaultFill(OCORRENCIAS);
            }};
    }

    private FilledRecord createBatchHeader(final BatchClosing batchClosing, final BankAccount bankAccount, final Person person, final Address address) {
        return new FilledRecord(getBatchHeader()) {{
                fill(CODIGO_BANCO, bankAccount.getBacenCode());
                defaultFill(LOTE_SERVICO);
                defaultFill(TIPO_REGISTRO);
                defaultFill(TIPO_OPERACAO);
                defaultFill(TIPO_SERVICO);
                fill(FORMA_LANCAMENTO,"1");
                defaultFill(LAYOUT_ARQUIVO);
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
                defaultFill(MENSAGEM);
                fill(LOGRADOURO, address.getStreetName());
                fill(NUMERO, address.getNumber());
                fill(COMPLEMENTO, address.getComplement());
                fill(CIDADE, address.getCity());
                fill(CEP, address.firstZipCode());
                fill(COMPLEMENTO_CEP,address.lastZipeCode());
                fill(ESTADO, address.getState().name());
                defaultFill(FIM_FEBRABAN);
                defaultFill(OCORRENCIAS);
            }};
    }

    private FilledRecord createRemittanceHeader(final BatchClosing batchClosing, final BankAccount bankAccount, final Person person) {
        return new FilledRecord(getRemittanceHeader()) {{
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
    }

    private FilledRecord createRemittanceTrailer(final BankAccount bankAccount) {
        return new FilledRecord(getRemittanceTrailer()) {{
            fill(CODIGO_BANCO, bankAccount.getBacenCode());
            defaultFill(LOTE_SERVICO);
            defaultFill(TIPO_REGISTRO);
            defaultFill(INICIO_FEBRABAN);
            fill(QUANTIDADE_LOTES,"1");
            fill(QUANTIDADE_REGISTROS, "4");
            defaultFill(QUANTIDADE_CONTAS);
            defaultFill(FIM_FEBRABAN);
        }};
    }
}
