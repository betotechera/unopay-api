package br.com.unopay.api.payment.cnab240;

import br.com.unopay.api.bacen.model.BankAccount;
import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.model.Address;
import br.com.unopay.api.model.BatchClosing;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.payment.model.PaymentRemittance;
import br.com.unopay.api.payment.model.PaymentRemittanceItem;
import java.text.SimpleDateFormat;
import java.util.Date;

import static br.com.unopay.api.payment.cnab240.RemittanceLayout.getBatchHeader;
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.getBatchSegmentA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.getBatchSegmentB;
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.getBatchTrailer;
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.getRemittanceHeader;
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.getRemittanceTrailer;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.AGENCIA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.AVISO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.BAIRRO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.BANCO_COMPENSACAO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.BANCO_FAVORECIDO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.CAMARA_CENTRALIZADORA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.CEP;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.CIDADE;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.CODIGO_DOCUMENTO_FAVORECIDO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.CODIGO_REMESSA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.COMPLEMENTO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.COMPLEMENTO_CEP;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.CONVEIO_BANCO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DATA_GERACAO_ARQUIVO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DATA_PAGAMENTO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DATA_REAL_PAGAMENTO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DATA_VENCIMENTO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DENSIDADE_GRAVACAO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DIGITO_AGENCIA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DIGITO_AGENCIA_CONTA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DIGITO_CONTA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DOCUMENTO_ATRIBUIDO_BANCO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DOCUMENTO_EMPRESA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.ESTADO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.FIM_FEBRABAN;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.FINALIDADE_DOC;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.FINALIDADE_TED;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.FORMA_LANCAMENTO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.HORA_GERACAO_ARQUIVO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.INFORMACAO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.INICIO_FEBRABAN;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.INSTITUICAO_MOVIMENTO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.LAYOUT_ARQUIVO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.LOGRADOURO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.LOTE_SERVICO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.MEIO_FEBRABAN;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.MENSAGEM;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NOME_BANCO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NOME_EMPRESA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NOME_FAVORECIDO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NUMERO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NUMERO_AVISO_DEBITO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NUMERO_CONTA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NUMERO_INSCRICAO_EMPRESA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NUMERO_INSCRICAO_FAVORECIDO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NUMERO_REGISTRO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.OCORRENCIAS;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.QUANTIDADE_CONTAS;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.QUANTIDADE_LOTES;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.QUANTIDADE_MOEDA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.QUANTIDADE_MOEDAS;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.QUANTIDADE_REGISTROS;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.RESERVADO_BANCO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.RESERVADO_EMPRESA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.SEGMENTO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.SEQUENCIAL_ARQUIVO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.SOMATORIA_VALORES;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.TIPO_INSCRICAO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.TIPO_INSCRICAO_FAVORECIDO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.TIPO_MOEDA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.TIPO_MOVIMENTO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.TIPO_OPERACAO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.TIPO_REGISTRO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.TIPO_SERVICO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.VALOR_ABATIMENTO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.VALOR_DESCONTO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.VALOR_DOCUMENTO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.VALOR_MORA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.VALOR_MULTA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.VALOR_PAGAMENTO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.VALOR_REAL_PAGAMENTO;

public class Cnab240Generator {

    private Date currentDate;

    public Cnab240Generator(Date currentDate){
        this.currentDate = currentDate;
    }

    public String generate(PaymentRemittance remittance) {
        FilledRecord remittanceHeader = createRemittanceHeader(remittance);
        WrappedRecord batch = createBatch(remittance);
        FilledRecord remittanceTrailer = createRemittanceTrailer(remittance);
        return new WrappedRecord()
                .createHeader(remittanceHeader)
                .addRecord(batch)
                .createTrailer(remittanceTrailer)
                .build();
    }

    private WrappedRecord createBatch(PaymentRemittance remittance) {
        FilledRecord batchHeader = createBatchHeader(remittance);
        FilledRecord segmentA = createSegmentA(remittance.getRemittanceItems().stream().findFirst().get());
        FilledRecord segmentB = createSegmentB(remittance.getRemittanceItems().stream().findFirst().get());
        FilledRecord batchTrailer = createBatchTrailer(remittance);
        return new WrappedRecord()
                .createHeader(batchHeader)
                .addRecord(segmentA)
                .addRecord(segmentB)
                .createTrailer(batchTrailer);
    }

    private FilledRecord createSegmentB(final PaymentRemittanceItem remittanceItem) {
        BankAccount bankAccount = remittanceItem.getEstablishment().getBankAccount();
        Person person = remittanceItem.getEstablishment().getPerson();
        Address address = person.getAddress();
        return new FilledRecord(getBatchSegmentB()) {{
                fill(BANCO_COMPENSACAO, bankAccount.getBacenCode());
                defaultFill(LOTE_SERVICO);
                defaultFill(TIPO_REGISTRO);
                fill(NUMERO_REGISTRO, "1");
                defaultFill(SEGMENTO);
                defaultFill(INICIO_FEBRABAN);
                defaultFill(TIPO_INSCRICAO_FAVORECIDO);
                fill(NUMERO_INSCRICAO_FAVORECIDO,person.getDocument().getNumber());
                fill(LOGRADOURO, address.getStreetName());
                fill(NUMERO, address.getNumber());
                fill(COMPLEMENTO, address.getComplement());
                fill(BAIRRO, address.getDistrict());
                fill(CIDADE, address.getCity());
                fill(CEP, address.firstZipCode());
                fill(COMPLEMENTO_CEP, address.lastZipeCode());
                fill(ESTADO, address.getState().name());
                fill(DATA_VENCIMENTO, new SimpleDateFormat("ddMMyyyy").format(currentDate));
                fill(VALOR_DOCUMENTO, remittanceItem.getValue().toString());
                defaultFill(VALOR_ABATIMENTO);
                defaultFill(VALOR_DESCONTO);
                defaultFill(VALOR_MORA);
                defaultFill(VALOR_MULTA);
                fill(CODIGO_DOCUMENTO_FAVORECIDO, person.getDocument().getNumber());
                defaultFill(FIM_FEBRABAN);
            }};
    }

    private FilledRecord createSegmentA(final PaymentRemittanceItem remittanceItem) {
        Establishment establishment = remittanceItem.getEstablishment();
        BankAccount bankAccount = establishment.getBankAccount();
        Person person = establishment.getPerson();
        return new FilledRecord(getBatchSegmentA()) {{
            fill(BANCO_COMPENSACAO, bankAccount.getBacenCode());
            defaultFill(LOTE_SERVICO);
            defaultFill(TIPO_REGISTRO);
            fill(NUMERO_REGISTRO, "1");
            defaultFill(SEGMENTO);
            defaultFill(TIPO_MOVIMENTO);
            defaultFill(INSTITUICAO_MOVIMENTO);
            defaultFill(CAMARA_CENTRALIZADORA);
            fill(BANCO_FAVORECIDO, bankAccount.getBacenCode());
            fill(AGENCIA, bankAccount.agentDvFirstDigit());
            fill(DIGITO_AGENCIA, bankAccount.agentDvLastDigit());
            fill(NUMERO_CONTA, bankAccount.getAccountNumber());
            fill(DIGITO_CONTA, bankAccount.accountDvFirstDigit());
            fill(DIGITO_AGENCIA_CONTA, bankAccount.accountDvLastDigit());
            fill(NOME_FAVORECIDO, person.getName());
            fill(DOCUMENTO_EMPRESA, person.getDocument().getNumber());
            fill(DATA_PAGAMENTO, new SimpleDateFormat("ddMMyyyy").format(currentDate));
            defaultFill(TIPO_MOEDA);
            fill(QUANTIDADE_MOEDA, remittanceItem.getValue().toString());
            fill(VALOR_PAGAMENTO, remittanceItem.getValue().toString());
            defaultFill(DOCUMENTO_ATRIBUIDO_BANCO);
            defaultFill(DATA_REAL_PAGAMENTO);
            fill(VALOR_REAL_PAGAMENTO, remittanceItem.getValue().toString());
            defaultFill(INFORMACAO);
            defaultFill(FINALIDADE_DOC);
            defaultFill(FINALIDADE_TED);
            defaultFill(FIM_FEBRABAN);
            defaultFill(AVISO);
            defaultFill(OCORRENCIAS);
        }};
    }

    private FilledRecord createBatchTrailer(final PaymentRemittance remittance) {
        BankAccount bankAccount = remittance.getIssuer().getPaymentAccount().getBankAccount();
        return new FilledRecord(getBatchTrailer()) {{
                fill(BANCO_COMPENSACAO, bankAccount.getBacenCode());
                fill(LOTE_SERVICO, "0009");
                defaultFill(TIPO_REGISTRO);
                defaultFill(INICIO_FEBRABAN);
                fill(SOMATORIA_VALORES,remittance.total().toString());
                fill(QUANTIDADE_MOEDAS, remittance.total().toString());
                defaultFill(NUMERO_AVISO_DEBITO);
                defaultFill(FIM_FEBRABAN);
                defaultFill(OCORRENCIAS);
            }};
    }

    private FilledRecord createBatchHeader(final PaymentRemittance remittance) {
        BankAccount bankAccount = remittance.getIssuer().getPaymentAccount().getBankAccount();
        Person person = remittance.getIssuer().getPerson();
        Address address = person.getAddress();
        return new FilledRecord(getBatchHeader()) {{
                fill(BANCO_COMPENSACAO, bankAccount.getBacenCode());
                defaultFill(LOTE_SERVICO);
                defaultFill(TIPO_REGISTRO);
                defaultFill(TIPO_OPERACAO);
                defaultFill(TIPO_SERVICO);
                fill(FORMA_LANCAMENTO,"1");
                defaultFill(LAYOUT_ARQUIVO);
                defaultFill(INICIO_FEBRABAN);
                defaultFill(TIPO_INSCRICAO);
                fill(NUMERO_INSCRICAO_EMPRESA, person.getDocument().getNumber());
                fill(CONVEIO_BANCO, remittance.getIssuer().getPaymentAccount().getBankAgreementNumber());
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

    private FilledRecord createRemittanceHeader(final PaymentRemittance remittance) {
        BankAccount bankAccount = remittance.getIssuer().getPaymentAccount().getBankAccount();
        Person person = remittance.getIssuer().getPerson();
        return new FilledRecord(getRemittanceHeader()) {{
                fill(BANCO_COMPENSACAO, bankAccount.getBacenCode());
                defaultFill(LOTE_SERVICO);
                defaultFill(TIPO_REGISTRO);
                defaultFill(INICIO_FEBRABAN);
                defaultFill(TIPO_INSCRICAO);
                fill(NUMERO_INSCRICAO_EMPRESA, person.getDocument().getNumber());
                fill(CONVEIO_BANCO, remittance.getIssuer().getPaymentAccount().getBankAgreementNumber());
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

    private FilledRecord createRemittanceTrailer(final PaymentRemittance remittance) {
        BankAccount bankAccount = remittance.getIssuer().getPaymentAccount().getBankAccount();
        return new FilledRecord(getRemittanceTrailer()) {{
            fill(BANCO_COMPENSACAO, bankAccount.getBacenCode());
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
