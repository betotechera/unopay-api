package br.com.unopay.api.billing.remittance.cnab240.mapped.itau;

import br.com.unopay.api.billing.remittance.cnab240.filler.FilledRecord;
import br.com.unopay.api.billing.remittance.model.PaymentRemittance;
import br.com.unopay.api.util.Rounder;

import static br.com.unopay.api.billing.remittance.cnab240.filler.ItauRemittanceLayout.getBatchTrailer;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BANCO_COMPENSACAO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BRANCOS_1;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BRANCOS_2;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.INICIO_FEBRABAN;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.LOTE_SERVICO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.OCORRENCIAS;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_REGISTROS;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.SOMATORIA_VALORES;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_REGISTRO;

public class ItauBatchTrailer {

    public ItauBatchTrailer(){}

    public FilledRecord create(final PaymentRemittance remittance, Integer batchNumber) {
        return new FilledRecord(getBatchTrailer()).
                defaultFill(BANCO_COMPENSACAO).
                fill(LOTE_SERVICO, batchNumber).
                defaultFill(TIPO_REGISTRO).
                defaultFill(INICIO_FEBRABAN).
                fill(QUANTIDADE_REGISTROS, remittance.getRemittanceItems().size()).
                fill(SOMATORIA_VALORES,Rounder.roundToString(remittance.getTotal())).
                defaultFill(BRANCOS_1).
                defaultFill(BRANCOS_2).
                defaultFill(OCORRENCIAS);
    }
}
