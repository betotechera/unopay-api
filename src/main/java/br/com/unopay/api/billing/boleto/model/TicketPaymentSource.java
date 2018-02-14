package br.com.unopay.api.billing.boleto.model;

import br.com.unopay.bootcommons.http.DescriptableEnum;

public enum TicketPaymentSource implements DescriptableEnum {

    HIRER_CREDIT("Contratante credito"), HIRER_INSTALLMENT("Contrante mensalidade"), CONTRACTOR_CREDIT("favorecido");

    private String description;

    TicketPaymentSource(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
