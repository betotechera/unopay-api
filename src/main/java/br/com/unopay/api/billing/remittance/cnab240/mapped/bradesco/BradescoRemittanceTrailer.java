package br.com.unopay.api.billing.remittance.cnab240.mapped.bradesco;

import br.com.unopay.api.billing.remittance.cnab240.filler.FilledRecord;
import br.com.unopay.api.billing.remittance.model.PaymentRemittance;

import static br.com.unopay.api.billing.remittance.cnab240.filler.BradescoRemittanceLayout.getRemittanceTrailer;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BANCO_COMPENSACAO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.FIM_FEBRABAN;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.INICIO_FEBRABAN;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.LOTE_SERVICO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_CONTAS;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_LOTES;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_REGISTROS;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_REGISTRO;

public class BradescoRemittanceTrailer {

    public static final int HEADERS_AND_TRAILERS = 4;
    public static final int SEGMENTS = 2;

    public BradescoRemittanceTrailer(){}

    public FilledRecord create(final PaymentRemittance remittance) {
        return new FilledRecord(getRemittanceTrailer()).
            fill(BANCO_COMPENSACAO, remittance.getPayer().getBankCode()).
            defaultFill(LOTE_SERVICO).
            defaultFill(TIPO_REGISTRO).
            defaultFill(INICIO_FEBRABAN).
            fill(QUANTIDADE_LOTES,"1").
            fill(QUANTIDADE_REGISTROS, remittance.getRemittanceItems().size() * SEGMENTS + HEADERS_AND_TRAILERS).
            defaultFill(QUANTIDADE_CONTAS).
            defaultFill(FIM_FEBRABAN);
    }
}
