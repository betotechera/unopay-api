package br.com.unopay.api.payment.cnab240.mapped;

import br.com.unopay.api.bacen.model.BankAccount;
import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.payment.cnab240.filler.FilledRecord;
import br.com.unopay.api.payment.model.PaymentRemittanceItem;
import br.com.unopay.api.util.Rounder;
import java.text.SimpleDateFormat;
import java.util.Date;

import static br.com.unopay.api.payment.cnab240.Cnab240Generator.DATE_FORMAT;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayout.getBatchSegmentA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.AGENCIA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.AVISO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.BANCO_COMPENSACAO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.BANCO_FAVORECIDO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.CAMARA_CENTRALIZADORA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DATA_PAGAMENTO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DATA_REAL_PAGAMENTO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DIGITO_AGENCIA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DIGITO_AGENCIA_CONTA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DIGITO_CONTA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DOCUMENTO_ATRIBUIDO_BANCO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DOCUMENTO_EMPRESA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.FIM_FEBRABAN;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.FINALIDADE_DOC;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.FINALIDADE_TED;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.INFORMACAO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.INSTITUICAO_MOVIMENTO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.LOTE_SERVICO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NOME_FAVORECIDO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NUMERO_CONTA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NUMERO_REGISTRO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.OCORRENCIAS;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_MOEDA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.SEGMENTO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.TIPO_MOEDA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.TIPO_MOVIMENTO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.TIPO_REGISTRO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.VALOR_PAGAMENTO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.VALOR_REAL_PAGAMENTO;

public class SegmentA {

    private Date currentDate;

    public SegmentA(Date currentDate){
        this.currentDate = currentDate;
    }

    public FilledRecord create(final PaymentRemittanceItem remittanceItem, Integer position) {
        Establishment establishment = remittanceItem.getEstablishment();
        BankAccount bankAccount = establishment.getBankAccount();
        Person person = establishment.getPerson();
        return new FilledRecord(getBatchSegmentA()) {{
            defaultFill(BANCO_COMPENSACAO);
            fill(LOTE_SERVICO, position);
            defaultFill(TIPO_REGISTRO);
            fill(NUMERO_REGISTRO, position);
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
            fill(DATA_PAGAMENTO, new SimpleDateFormat(DATE_FORMAT).format(currentDate));
            defaultFill(TIPO_MOEDA);
            defaultFill(QUANTIDADE_MOEDA);
            fill(VALOR_PAGAMENTO, Rounder.roundToString(remittanceItem.getValue()));
            defaultFill(DOCUMENTO_ATRIBUIDO_BANCO);
            defaultFill(DATA_REAL_PAGAMENTO);
            fill(VALOR_REAL_PAGAMENTO, Rounder.roundToString(remittanceItem.getValue()));
            defaultFill(INFORMACAO);
            defaultFill(FINALIDADE_DOC);
            defaultFill(FINALIDADE_TED);
            defaultFill(FIM_FEBRABAN);
            defaultFill(AVISO);
            defaultFill(OCORRENCIAS);
        }};
    }
}
