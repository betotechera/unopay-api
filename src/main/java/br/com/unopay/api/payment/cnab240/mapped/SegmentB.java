package br.com.unopay.api.payment.cnab240.mapped;

import br.com.unopay.api.model.Address;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.payment.cnab240.filler.FilledRecord;
import br.com.unopay.api.payment.model.PaymentRemittanceItem;
import br.com.unopay.api.util.Rounder;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.ObjectUtils;

import static br.com.unopay.api.payment.cnab240.Cnab240Generator.DATE_FORMAT;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayout.getBatchSegmentB;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.BAIRRO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.BANCO_COMPENSACAO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.CEP;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.CIDADE;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.CODIGO_DOCUMENTO_FAVORECIDO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.COMPLEMENTO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.COMPLEMENTO_CEP;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DATA_VENCIMENTO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.ESTADO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.FIM_FEBRABAN;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.INICIO_FEBRABAN;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.LOGRADOURO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.LOTE_SERVICO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NUMERO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NUMERO_INSCRICAO_FAVORECIDO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NUMERO_REGISTRO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.SEGMENTO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.TIPO_INSCRICAO_FAVORECIDO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.TIPO_REGISTRO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.VALOR_ABATIMENTO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.VALOR_DESCONTO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.VALOR_DOCUMENTO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.VALOR_MORA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.VALOR_MULTA;

public class SegmentB {

    private Date currentDate;

    public SegmentB(){}

    public SegmentB(Date currentDate){
        this.currentDate = ObjectUtils.clone(currentDate);
    }

    public FilledRecord create(final PaymentRemittanceItem remittanceItem, Integer position) {
        Person person = remittanceItem.getEstablishment().getPerson();
        Address address = person.getAddress();
        return new FilledRecord(getBatchSegmentB()).
            defaultFill(BANCO_COMPENSACAO).
            fill(LOTE_SERVICO, position).
            defaultFill(TIPO_REGISTRO).
            fill(NUMERO_REGISTRO, position).
            defaultFill(SEGMENTO).
            defaultFill(INICIO_FEBRABAN).
            defaultFill(TIPO_INSCRICAO_FAVORECIDO).
            fill(NUMERO_INSCRICAO_FAVORECIDO,person.getDocument().getNumber()).
            fill(LOGRADOURO, address.getStreetName()).
            fill(NUMERO, address.getNumber()).
            fill(COMPLEMENTO, address.getComplement()).
            fill(BAIRRO, address.getDistrict()).
            fill(CIDADE, address.getCity()).
            fill(CEP, address.firstZipCode()).
            fill(COMPLEMENTO_CEP, address.lastZipeCode()).
            fill(ESTADO, address.getState().name()).
            fill(DATA_VENCIMENTO, new SimpleDateFormat(DATE_FORMAT).format(currentDate)).
            fill(VALOR_DOCUMENTO, Rounder.roundToString(remittanceItem.getValue())).
            defaultFill(VALOR_ABATIMENTO).
            defaultFill(VALOR_DESCONTO).
            defaultFill(VALOR_MORA).
            defaultFill(VALOR_MULTA).
            fill(CODIGO_DOCUMENTO_FAVORECIDO, person.getDocument().getNumber()).
            defaultFill(FIM_FEBRABAN);
    }
}
