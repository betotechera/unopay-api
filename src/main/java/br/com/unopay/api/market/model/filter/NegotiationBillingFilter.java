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

    @SearchableField(field = "hirerNegotiation.product.issuer.person.document.number")
    private String issuerDocument;

    @SearchableField(field = "hirerNegotiation.hirer.person.document.number")
    private String hirerDocument;

    @SearchableField
    private PaymentStatus status;

    @SearchableField
    private Period createdDateTime;

    @SearchableField
    private Integer installmentNumber;
}
