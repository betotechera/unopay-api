package br.com.unopay.api.billing.model;

import java.util.Locale;

public enum CurrencyCode {

    BRL("BRL","Brazil Real", new Locale("pt", "BR")),
    EUR("EUR","Euro Member Countries"),
    USD("USD","United States Dollar", new Locale("en", "US"));

    private String code;
    private String description;
    private Locale locale;

    CurrencyCode(String code, String description, Locale locale) {
        this.code = code;
        this.description = description;
        this.locale = locale;
    }

    CurrencyCode(String code, String description) {
        this(code, description, null);
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
}
