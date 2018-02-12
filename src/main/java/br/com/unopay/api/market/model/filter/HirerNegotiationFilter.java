package br.com.unopay.api.market.model.filter;

import br.com.unopay.bootcommons.repository.filter.SearchableField;
import lombok.Data;

@Data
public class HirerNegotiationFilter {

    @SearchableField(field = "product.issuer.id")
    private String issuer;

    @SearchableField(field = "product.id")
    private String product;

    @SearchableField(field = "hirer.id")
    private String hirer;

    @SearchableField(field = "hirer.person.document.number")
    private String hirerDocument;

}
