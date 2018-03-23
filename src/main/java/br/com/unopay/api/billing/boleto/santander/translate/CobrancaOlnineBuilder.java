package br.com.unopay.api.billing.boleto.santander.translate;

import br.com.unopay.api.bacen.model.PaymentBankAccount;
import br.com.unopay.api.billing.boleto.santander.cobrancaonline.dl.TicketRequest;
import br.com.unopay.api.billing.boleto.santander.service.CobrancaOnlineService;
import br.com.unopay.api.model.Person;
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

    public static final String DS_SERVICES = "04";
    public static final String ZERO = "0000000000000";
    public static final String EMPTY = "";
    public static final String DOT = ".";
    private PaymentBankAccount paymentBankAccount;
    private Person payer;
    private BigDecimal value;
    private Integer expirationDays;
    private String yourNumber;

    public CobrancaOlnineBuilder payer(Person payer) {
        this.payer = payer;
        return this;
    }

    public CobrancaOlnineBuilder paymentBankAccount(PaymentBankAccount paymentBankAccount) {
        this.paymentBankAccount = paymentBankAccount;
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


    public List<TicketRequest.Dados.Entry> build() {
        List<TicketRequest.Dados.Entry> entries = new ArrayList<>();
        new TicketRequest.Dados.Entry();
        entries.add(entry("PAGADOR.NOME", payer.getName()));
        entries.add(entry("PAGADOR.TP-DOC", payer.documentType()));
        entries.add(entry("PAGADOR.NUM-DOC", payer.documentNumber()));
        entries.add(entry("PAGADOR.BAIRRO", payer.getAddress().getDistrict()));
        entries.add(entry("PAGADOR.CIDADE", payer.getAddress().getCity()));
        entries.add(entry("PAGADOR.ENDER", payer.getAddress().getStreetName()));
        entries.add(entry("PAGADOR.CEP", payer.getAddress().getZipCode()));
        entries.add(entry("PAGADOR.UF", payer.getAddress().getState().name()));
        entries.add(entry("CONVENIO.COD-CONVENIO",paymentBankAccount.getBankAgreementNumberForDebit()));
        entries.add(entry("CONVENIO.COD-BANCO", paymentBankAccount.getBankAccount().getBank()
                                                    .getBacenCode().toString()));
        entries.add(entry("TITULO.DT-VENCTO", format(new DateTime().plusDays(expirationDays).toDate())));
        entries.add(entry("TITULO.DT-EMISSAO",format(new Date())));
        entries.add(entry("TITULO.SEU-NUMERO",this.yourNumber));
        entries.add(entry("TITULO.NOSSO-NUMERO", ZERO));
        entries.add(entry("TITULO.ESPECIE", DS_SERVICES));
        entries.add(entry("TITULO.VL-NOMINAL",Rounder.roundToString(value).replace(DOT, EMPTY)));
        entries.add(entry("TITULO.TP-DESC","0"));
        entries.add(entry("TITULO.TP-PROTESTO","3"));
        entries.add(entry("TITULO.QT-DIAS-BAIXA","2"));
        return entries;
    }

    private TicketRequest.Dados.Entry entry(String key, String value){
        return new TicketRequest.Dados.Entry().key(key).value(value);
    }

    private String format(Date date){
        return new SimpleDateFormat(CobrancaOnlineService.DD_MM_YYYY).format(date);
    }

    public CobrancaOlnineBuilder yourNumber(String yourNumber) {
        this.yourNumber = yourNumber;
        return this;
    }
}
