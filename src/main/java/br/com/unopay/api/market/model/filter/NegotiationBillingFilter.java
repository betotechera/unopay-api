package br.com.unopay.api.market.model.filter;

import br.com.unopay.bootcommons.repository.filter.SearchableField;
import lombok.Data;

@Data
public class NegotiationBillingFilter {

    @SearchableField(field = "hirerNegotiation.product.issuer.id")
    private String issuer;
}
