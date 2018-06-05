package br.com.unopay.api.market.model.filter;

import br.com.unopay.api.order.model.PaymentStatus;
import br.com.unopay.bootcommons.model.Period;
import br.com.unopay.bootcommons.repository.filter.SearchableField;
import lombok.Data;

@Data
public class BonusBillingFilter {
    @SearchableField(field = "payer.document.number")
    private String document;

    @SearchableField(field = "issuer.person.document.number")
    private String issuer;

    @SearchableField
    private PaymentStatus status;

    @SearchableField(field = "createdDateTime")
    private Period createdDateTimePeriod;

    @SearchableField(field = "expiration")
    private Period expirationPeriod;
}
