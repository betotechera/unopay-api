package br.com.unopay.api.billing.boleto.itau.mapping;

import br.com.itau.autorizador.model.Beneficiario;
import br.com.itau.autorizador.model.Cobranca;
import br.com.itau.autorizador.model.Juros;
import br.com.itau.autorizador.model.Moeda;
import br.com.itau.autorizador.model.Pagador;
import br.com.itau.autorizador.model.RecebimentoDivergente;
import br.com.unopay.api.bacen.model.PaymentBankAccount;
import br.com.unopay.api.billing.boleto.santander.cobrancaonline.dl.TicketRequest;
import br.com.unopay.api.billing.boleto.santander.service.CobrancaOnlineService;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.util.Rounder;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.joda.time.DateTime;

public class ItauBuilder {

    public static final String ZERO = "0000000000000";
    public static final String EMPTY = "";
    public static final String DOT = ".";
    private PaymentBankAccount paymentBankAccount;
    private Person payer;
    private BigDecimal value;
    private Integer expirationDays;
    private String yourNumber;

    public ItauBuilder payer(Person payer) {
        this.payer = payer;
        return this;
    }

    public ItauBuilder paymentBankAccount(PaymentBankAccount paymentBankAccount) {
        this.paymentBankAccount = paymentBankAccount;
        return this;
    }

    public ItauBuilder expirationDays(Integer expirationDays) {
        this.expirationDays = expirationDays;
        return this;
    }


    public ItauBuilder value(BigDecimal value) {
        this.value = value;
        return this;
    }


    public Cobranca build() {
        Cobranca cobranca = new Cobranca();
        new TicketRequest.Dados.Entry();
        Beneficiario beneficiario = new Beneficiario();
        Pagador pagador = new Pagador();
        pagador.setCpfCnpjPagador(payer.documentNumber());
        pagador.setNomePagador(payer.getShortName());
        pagador.setLogradouroPagador(payer.getAddress().getStreetName());
        pagador.setCidadePagador(payer.getAddress().getCity());
        pagador.setUfPagador(payer.getAddress().getState().name());
        pagador.setCepPagador(payer.getAddress().getZipCode());
        beneficiario.setAgenciaBeneficiario(paymentBankAccount.getBankAccount().getAgency());
        beneficiario.setContaBeneficiario(paymentBankAccount.getBankAccount().getAccountNumber());
        beneficiario.setDigitoVerificadorContaBeneficiario(paymentBankAccount.getBankAccount().getAgencyDigit());
        cobranca.setDataVencimento(format(new DateTime().plusDays(expirationDays).toDate()));
        cobranca.setDataEmissao(format(new Date()));
        cobranca.setSeuNumero(this.yourNumber);
        cobranca.setNossoNumero(ZERO);
        cobranca.setMoeda(new Moeda());
        cobranca.setJuros(new Juros());
        cobranca.setBeneficiario(beneficiario);
        cobranca.setPagador(pagador);
        cobranca.setRecebimentoDivergente(new RecebimentoDivergente());
        cobranca.setValorCobrado(Rounder.roundToString(value).replace(DOT, EMPTY));
        return cobranca;
    }

    private String format(Date date){
        return new SimpleDateFormat(CobrancaOnlineService.DD_MM_YYYY).format(date);
    }

    public ItauBuilder yourNumber(String yourNumber) {
        this.yourNumber = yourNumber;
        return this;
    }
}
