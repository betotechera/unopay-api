package br.com.unopay.api.payment.cnab240.mapped;

import br.com.unopay.api.bacen.model.BankAccount;
import br.com.unopay.api.model.Address;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.payment.cnab240.filler.FilledRecord;
import br.com.unopay.api.payment.model.PaymentRemittance;

import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayout.getBatchHeader;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.AGENCIA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.BANCO_COMPENSACAO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.CEP;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.CIDADE;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.COMPLEMENTO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.COMPLEMENTO_CEP;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.CONVEIO_BANCO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DIGITO_AGENCIA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DIGITO_AGENCIA_CONTA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DIGITO_CONTA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.ESTADO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.FIM_FEBRABAN;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.FORMA_LANCAMENTO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.INICIO_FEBRABAN;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.LAYOUT_ARQUIVO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.LOGRADOURO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.LOTE_SERVICO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.MENSAGEM;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NOME_EMPRESA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NUMERO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NUMERO_CONTA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NUMERO_INSCRICAO_EMPRESA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.OCORRENCIAS;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.TIPO_INSCRICAO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.TIPO_OPERACAO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.TIPO_REGISTRO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.TIPO_SERVICO;

public class BatchHeader {

    public FilledRecord create(final PaymentRemittance remittance) {
        BankAccount bankAccount = remittance.getIssuer().getPaymentAccount().getBankAccount();
        Person person = remittance.getIssuer().getPerson();
        Address address = person.getAddress();
        return new FilledRecord(getBatchHeader()) {{
            defaultFill(BANCO_COMPENSACAO);
            defaultFill(LOTE_SERVICO);
            defaultFill(TIPO_REGISTRO);
            fill(TIPO_OPERACAO, remittance.getOperationType().getCode());
            fill(TIPO_SERVICO, remittance.getPaymentServiceType().getCode());
            fill(FORMA_LANCAMENTO,remittance.getTransferOption().getCode());
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
}
