package br.com.unopay.api.billing.creditcard.model;

import java.util.Locale;

public enum CurrencyCode {

    BRL("BRL",986,"Brazil Real", new Locale("pt", "BR")),
    EUR("EUR",978, "Euro Member Countries"),
    USD("USD",840, "United States Dollar", new Locale("en", "US"));

    private String code;
    private Integer iso;
    private String description;
    private Locale locale;

    CurrencyCode(String code, Integer iso, String description, Locale locale) {
        this.code = code;
        this.iso = iso;
        this.description = description;
        this.locale = locale;
    }

    CurrencyCode(String code,Integer iso, String description) {
        this(code, iso, description, null);
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public Locale getLocale() {
        return locale;
    }

    public Integer getIso() {
        return iso;
    }
}
