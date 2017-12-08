package br.com.unopay.api.billing.boleto.santander.translate;

import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.billing.boleto.santander.cobrancaonline.dl.TicketRequest;
import br.com.unopay.api.util.Rounder;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

@Component
public class CobrancaOlnineBuilder {

    private String ourNumber;
    private String yourNumber;
    private Issuer issuer;
    private BigDecimal value;
    private Integer expirationDays;
    private String number;

    public CobrancaOlnineBuilder issuer(Issuer issuer) {
        this.issuer = issuer;
        return this;
    }

    public CobrancaOlnineBuilder number(String number) {
        this.number = number;
        return this;
    }

    public CobrancaOlnineBuilder expirationDays(Integer expirationDays) {
        this.expirationDays = expirationDays;
        return this;
    }


    public CobrancaOlnineBuilder value(BigDecimal value) {
        this.value = value;
        return this;
    }

    public CobrancaOlnineBuilder ourNumber(String ourNumber) {
        this.ourNumber = ourNumber;
        return this;
    }

    public List<TicketRequest.Dados.Entry> build() {
        List<TicketRequest.Dados.Entry> entries = new ArrayList<>();
        new TicketRequest.Dados.Entry();
        entries.add(entry("PAGADOR.NOME",issuer.getPerson().getName()));
        entries.add(entry("PAGADOR.TP-DOC",issuer.getPerson().documentType()));
        entries.add(entry("PAGADOR.NUM-DOC",issuer.getPerson().documentNumber()));
        entries.add(entry("PAGADOR.BAIRRO",issuer.getPerson().getAddress().getDistrict()));
        entries.add(entry("PAGADOR.CIDADE",issuer.getPerson().getAddress().getCity()));
        entries.add(entry("PAGADOR.ENDER",issuer.getPerson().getAddress().getStreetName()));
        entries.add(entry("PAGADOR.CEP",issuer.getPerson().getAddress().getZipCode()));
        entries.add(entry("PAGADOR.UF",issuer.getPerson().getAddress().getState().name()));
        entries.add(entry("CONVENIO.COD-CONVENIO","9195386"));
        entries.add(entry("CONVENIO.COD-BANCO","033"));
        entries.add(entry("TITULO.NOSSO-NUMERO",ourNumber));
        entries.add(entry("TITULO.SEU-NUMERO",yourNumber));
        entries.add(entry("TITULO.DT-VENCTO", format(new DateTime().plusDays(expirationDays).toDate())));
        entries.add(entry("TITULO.DT-EMISSAO",format(new Date())));
        entries.add(entry("TITULO.ESPECIE","99"));
        entries.add(entry("TITULO.VL-NOMINAL",Rounder.roundToString(value)));
        entries.add(entry("TITULO.TP-DESC","0"));
        entries.add(entry("TITULO.TP-PROTESTO","3"));
        entries.add(entry("TITULO.QT-DIAS-BAIXA","2"));
        return entries;
    }

    private TicketRequest.Dados.Entry entry(String key, String value){
        return new TicketRequest.Dados.Entry().key(key).value(value);
    }

    private String format(Date date){
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    public CobrancaOlnineBuilder yourNumber(String yourNumber) {
        this.yourNumber = yourNumber;
        return this;
    }
}
