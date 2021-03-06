package br.com.unopay.api.credit.model;

import br.com.unopay.api.http.DescriptableEnum;
import java.util.Arrays;

public enum CreditInsertionType implements DescriptableEnum {
    BOLETO("oleto"), DIRECT_DEBIT("Debito em conta."),
    CREDIT_CARD("Cartao de credito.");

    private String description;

    CreditInsertionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPaymentProcessedByClient() {
        return Arrays.asList(BOLETO, CREDIT_CARD).contains(this);
    }
}
