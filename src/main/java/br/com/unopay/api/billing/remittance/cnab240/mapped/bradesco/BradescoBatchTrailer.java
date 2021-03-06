package br.com.unopay.api.billing.remittance.cnab240.mapped.bradesco;

import br.com.unopay.api.billing.remittance.cnab240.filler.FilledRecord;
import br.com.unopay.api.billing.remittance.model.PaymentRemittance;
import br.com.unopay.api.util.Rounder;

import static br.com.unopay.api.billing.remittance.cnab240.filler.BradescoRemittanceLayout.getBatchTrailer;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BANCO_COMPENSACAO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.FIM_FEBRABAN;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.INICIO_FEBRABAN;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.LOTE_SERVICO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO_AVISO_DEBITO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.OCORRENCIAS;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_MOEDAS;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_REGISTROS;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.SOMATORIA_VALORES;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_REGISTRO;
import static br.com.unopay.api.billing.remittance.cnab240.mapped.bradesco.BradescoRemittanceTrailer.HEADERS_AND_TRAILERS;
import static br.com.unopay.api.billing.remittance.cnab240.mapped.bradesco.BradescoRemittanceTrailer.SEGMENTS;

public class BradescoBatchTrailer {

    public BradescoBatchTrailer(){}

    public FilledRecord create(final PaymentRemittance remittance, Integer position) {
        return new FilledRecord(getBatchTrailer()).
            fill(BANCO_COMPENSACAO, remittance.getPayer().getBankCode()).
            fill(LOTE_SERVICO, position).
            defaultFill(TIPO_REGISTRO).
            defaultFill(INICIO_FEBRABAN).
            fill(SOMATORIA_VALORES, Rounder.roundToString(remittance.getTotal())).
            fill(QUANTIDADE_MOEDAS, Rounder.roundToString(remittance.getTotal())).
            fill(QUANTIDADE_REGISTROS, remittance.getRemittanceItems().size() * SEGMENTS + HEADERS_AND_TRAILERS).
            defaultFill(NUMERO_AVISO_DEBITO).
            defaultFill(FIM_FEBRABAN).
            defaultFill(OCORRENCIAS);
    }
}
