package br.com.unopay.api.market.model.filter;

import br.com.unopay.api.order.model.PaymentStatus;
import br.com.unopay.bootcommons.model.Period;
import br.com.unopay.bootcommons.repository.filter.SearchableField;
import lombok.Data;

@Data
public class NegotiationBillingFilter {

    @SearchableField(field = "hirerNegotiation.product.issuer.id")
    private String issuer;

    @SearchableField(field = "hirerNegotiation.hirer.id")
    private String hirer;

    @SearchableField(field = "hirerNegotiation.issuerDocumentNumber")
    private String issuerDocumentNumber;

    @SearchableField(field = "hirerNegotiation.hirerDocumentNumber")
    private String hirerDocumentNumber;

    @SearchableField
    private PaymentStatus status;

    @SearchableField(field = "createdDateTime")
    private Period createdDateTimePeriod;

    @SearchableField(field = "installmentExpiration")
    private Period installmentExpirationPeriod;

    @SearchableField
    private Integer installmentNumber;
}
