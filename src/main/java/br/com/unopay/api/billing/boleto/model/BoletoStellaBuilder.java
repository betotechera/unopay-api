package br.com.unopay.api.billing.boleto.model;


import br.com.caelum.stella.boleto.Banco;
import br.com.caelum.stella.boleto.Beneficiario;
import br.com.caelum.stella.boleto.Boleto;
import br.com.caelum.stella.boleto.Datas;
import br.com.caelum.stella.boleto.Endereco;
import br.com.caelum.stella.boleto.Pagador;
import br.com.caelum.stella.boleto.bancos.Santander;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.PaymentBankAccount;
import br.com.unopay.api.model.Address;
import br.com.unopay.api.model.Person;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import java.math.BigDecimal;
import org.joda.time.DateTime;

import static br.com.unopay.api.uaa.exception.Errors.CLIENT_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.EXPIRATION_DAYS_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.ISSUER_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.NUMBER_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.VALUE_REQUIRED;

public class BoletoStellaBuilder {

    private Issuer issuer;
    private Person client;
    private BigDecimal value;
    private String number;
    private Integer expirationDays;

    public BoletoStellaBuilder issuer(Issuer issuer) {
        this.issuer = issuer;
        return this;
    }

    public BoletoStellaBuilder number(String number) {
        this.number = number;
        return this;
    }

    public BoletoStellaBuilder expirationDays(Integer expirationDays) {
        this.expirationDays = expirationDays;
        return this;
    }

    public BoletoStellaBuilder client(Person client) {
        this.client = client;
        return this;
    }

    public BoletoStellaBuilder value(BigDecimal value) {
        this.value = value;
        return this;
    }

    public Boleto build() {
        checkUp();
        Datas datas = getDatas();

        Address beneficiaryAddress = issuer.getPerson().getAddress();
        Endereco enderecoBeneficiario = getEndereco(beneficiaryAddress);

        PaymentBankAccount paymentAccount = issuer.getPaymentAccount();
        Beneficiario beneficiario = getBeneficiario(enderecoBeneficiario, paymentAccount);

        Address payerAddress = this.client.getAddress();
        Endereco enderecoPagador = getEndereco(payerAddress);

        Pagador pagador = Pagador.novoPagador()
                .comNome(this.client.getName())
                .comDocumento(this.client.documentNumber())
                .comEndereco(enderecoPagador);

        Banco banco = new Santander();

        return Boleto.novoBoleto()
                .comBanco(banco)
                .comDatas(datas)
                .comBeneficiario(beneficiario)
                .comPagador(pagador)
                .comValorBoleto(this.value)
                .comNumeroDoDocumento(this.number);
    }

    private void checkUp() {
        if(this.value == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(VALUE_REQUIRED);
        }
        if(this.issuer == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(ISSUER_REQUIRED);
        }
        if(this.client == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(CLIENT_REQUIRED);
        }
        if(this.number == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(NUMBER_REQUIRED);
        }
        if(this.expirationDays == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(EXPIRATION_DAYS_REQUIRED);
        }
    }

    private Datas getDatas() {
        int dayOfMonth = DateTime.now().getDayOfMonth();
        int monthOfYear = DateTime.now().getMonthOfYear();
        int year = DateTime.now().getYear();

        int expirationDay = DateTime.now().plusDays(this.expirationDays).getDayOfMonth();
        int expirationMonth = DateTime.now().plusDays(this.expirationDays).getMonthOfYear();
        int expirationYear = DateTime.now().plusDays(this.expirationDays).getYear();
        return Datas.novasDatas()
                .comDocumento(dayOfMonth, monthOfYear, year)
                .comProcessamento(dayOfMonth, monthOfYear, year)
                .comVencimento(expirationDay, expirationMonth, expirationYear);
    }

    private Beneficiario getBeneficiario(Endereco enderecoBeneficiario, PaymentBankAccount paymentAccount) {
        return Beneficiario.novoBeneficiario()
                .comNomeBeneficiario(issuer.getPerson().getName())
                .comDocumento(issuer.documentNumber())
                .comAgencia(paymentAccount.getBankAccount().getAgency())
                .comDigitoAgencia(paymentAccount.getBankAccount().getAgencyDigit())
                .comCodigoBeneficiario(paymentAccount.getBeneficiaryCode())
                .comDigitoCodigoBeneficiario(paymentAccount.getBeneficiaryDigit())
                .comNumeroConvenio(paymentAccount.getBankAgreementNumberForDebit())
                .comCarteira(paymentAccount.getWalletNumber())
                .comEndereco(enderecoBeneficiario);
    }

    private Endereco getEndereco(Address beneficiaryAddress) {
        return Endereco.novoEndereco()
                    .comLogradouro(beneficiaryAddress.getStreetName())
                    .comBairro(beneficiaryAddress.getDistrict())
                    .comCep(beneficiaryAddress.getZipCode())
                    .comCidade(beneficiaryAddress.getCity())
                    .comUf(beneficiaryAddress.getState().name());
    }
}
