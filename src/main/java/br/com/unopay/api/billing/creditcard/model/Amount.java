package br.com.unopay.api.billing.creditcard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

@Embeddable
public class Amount implements Serializable {

    private static final long serialVersionUID = 1;
    @NotNull
    @Column(name = "currency")
    @Enumerated(EnumType.STRING)
    private CurrencyCode currency;

    @NotNull
    @Column(name = "value")
    private BigDecimal value;

    protected Amount() {}

    public Amount(CurrencyCode currency, BigDecimal value) {
        this.currency = currency;
        this.value = value;
    }

    public static Amount of(CurrencyCode currency, BigDecimal value) {
        return new Amount(currency, value);
    }

    public static Amount of(Amount amount) {
        return new Amount(amount.getCurrency(), amount.getValue());
    }

    public BigDecimal getValue() {
        return value;
    }

    public CurrencyCode getCurrency() {
        return currency;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public void setCurrency(CurrencyCode currency) {
        this.currency = currency;
    }

    @JsonIgnore
    public String getValueFormatted() {
        return NumberFormat.getCurrencyInstance(currency.getLocale()).format(value);
    }

    @Override
    public String toString() {
        return "Amount{" +
                "currency='" + currency + '\'' +
                ", value=" + value +
                '}';
    }
}
