package br.com.unopay.api.bacen.model.filter;

import br.com.unopay.bootcommons.repository.filter.SearchableField;
import lombok.Data;

@Data
public class AccreditedNetworkIssuerFilter {

    @SearchableField(field = "issuer.id")
    private String issuer;
}
