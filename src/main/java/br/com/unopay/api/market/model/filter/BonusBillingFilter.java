package br.com.unopay.api.market.model.filter;

import br.com.unopay.bootcommons.repository.filter.SearchableField;
import lombok.Data;

@Data
public class BonusBillingFilter {
    @SearchableField(field = "person.document.number")
    private String document;
}
