package br.com.unopay.api.billing.remittance.cnab240.mapped.itau;

import br.com.unopay.api.billing.remittance.cnab240.filler.FilledRecord;
import br.com.unopay.api.billing.remittance.model.PaymentRemittance;

import static br.com.unopay.api.billing.remittance.cnab240.filler.ItauRemittanceLayout.getRemittanceTrailer;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BANCO_COMPENSACAO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BRANCOS_1;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.INICIO_FEBRABAN;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.LOTE_SERVICO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_LOTES;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_REGISTROS;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_REGISTRO;

public class ItauRemittanceTrailer {

    public static final int BATCH_ITEMS = 3;
    public static final int HEADER_AND_TRAILER = 2;

    public ItauRemittanceTrailer(){}

    public FilledRecord create(final PaymentRemittance remittance) {
        return new FilledRecord(getRemittanceTrailer()).
                defaultFill(BANCO_COMPENSACAO).
                defaultFill(LOTE_SERVICO).
                defaultFill(TIPO_REGISTRO).
                defaultFill(INICIO_FEBRABAN).
                fill(QUANTIDADE_LOTES,remittance.getRemittanceItems().size()).
                fill(QUANTIDADE_REGISTROS, (remittance.getRemittanceItems().size() * BATCH_ITEMS)+HEADER_AND_TRAILER).
                defaultFill(BRANCOS_1);

    }
}
