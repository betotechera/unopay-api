package br.com.unopay.api.payment.cnab240.mapped;

import br.com.unopay.api.bacen.model.BankAccount;
import br.com.unopay.api.payment.cnab240.filler.FilledRecord;
import br.com.unopay.api.payment.model.PaymentRemittance;

import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayout.getBatchTrailer;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.BANCO_COMPENSACAO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.FIM_FEBRABAN;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.INICIO_FEBRABAN;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.LOTE_SERVICO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NUMERO_AVISO_DEBITO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.OCORRENCIAS;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_MOEDAS;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_REGISTROS;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.SOMATORIA_VALORES;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.TIPO_REGISTRO;
import static br.com.unopay.api.payment.cnab240.mapped.RemittanceTrailer.HEADERS_AND_TRAILERS;
import static br.com.unopay.api.payment.cnab240.mapped.RemittanceTrailer.SEGMENTS;

public class BatchTrailer {

    public FilledRecord create(final PaymentRemittance remittance) {
        int segments = 2;
        int myPosition = 1;
        int headers = 2;
        return new FilledRecord(getBatchTrailer()) {{
            defaultFill(BANCO_COMPENSACAO);
            fill(LOTE_SERVICO, remittance.getRemittanceItems().size() * segments + headers + myPosition);
            defaultFill(TIPO_REGISTRO);
            defaultFill(INICIO_FEBRABAN);
            fill(SOMATORIA_VALORES,remittance.total().toString());
            fill(QUANTIDADE_MOEDAS, remittance.total().toString());
            fill(QUANTIDADE_REGISTROS, remittance.getRemittanceItems().size() * SEGMENTS + HEADERS_AND_TRAILERS);
            defaultFill(NUMERO_AVISO_DEBITO);
            defaultFill(FIM_FEBRABAN);
            defaultFill(OCORRENCIAS);
        }};
    }
}
