package br.com.unopay.api.model.filter;

import br.com.unopay.api.model.BatchClosingSituation;
import br.com.unopay.bootcommons.model.Period;
import br.com.unopay.bootcommons.repository.filter.SearchableField;
import lombok.Data;

@Data
public class BatchClosingFilter {

    @SearchableField(field = "establishment.id")
    private String establishment;

    @SearchableField(field = "issuer.id")
    private String issuer;

    @SearchableField(field = "hirer.id")
    private String hirer;

    @SearchableField(field = "accreditedNetwork.id")
    private String accreditedNetwork;

    @SearchableField(field = "closingDateTime")
    private Period closingDateTimePeriod;

    @SearchableField
    private BatchClosingSituation situation;

    @SearchableField
    private String number;

}
