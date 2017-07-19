package br.com.unopay.api.payment.cnab240.mapped;

import br.com.unopay.api.bacen.model.BankAccount;
import br.com.unopay.api.payment.cnab240.filler.FilledRecord;
import br.com.unopay.api.payment.model.PaymentRemittance;

import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayout.getRemittanceTrailer;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.BANCO_COMPENSACAO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.FIM_FEBRABAN;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.INICIO_FEBRABAN;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.LOTE_SERVICO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_CONTAS;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_LOTES;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_REGISTROS;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.TIPO_REGISTRO;

public class RemittanceTrailer {

    public static final int HEADERS_AND_TRAILERS = 4;
    public static final int SEGMENTS = 2;

    public FilledRecord create(final PaymentRemittance remittance) {
        return new FilledRecord(getRemittanceTrailer()) {{
            defaultFill(BANCO_COMPENSACAO);
            defaultFill(LOTE_SERVICO);
            defaultFill(TIPO_REGISTRO);
            defaultFill(INICIO_FEBRABAN);
            fill(QUANTIDADE_LOTES,"1");
            fill(QUANTIDADE_REGISTROS, remittance.getRemittanceItems().size() * SEGMENTS + HEADERS_AND_TRAILERS);
            defaultFill(QUANTIDADE_CONTAS);
            defaultFill(FIM_FEBRABAN);
        }};
    }
}
