package br.com.unopay.api.billing.remittance.model.filter;


import br.com.unopay.api.billing.remittance.model.RemittanceSituation;
import br.com.unopay.bootcommons.model.Period;
import br.com.unopay.bootcommons.repository.filter.SearchableField;
import lombok.Data;

@Data
public class PaymentRemittanceFilter {

    @SearchableField(field = "remittanceItems.establishment.id")
    private String establishment;

    @SearchableField(field = "remittanceItems.establishment.accreditedNetwork.id")
    private String accreditedNetwork;

    @SearchableField(field = "issuer.id")
    private String issuer;

    @SearchableField
    private String number;

    @SearchableField
    private RemittanceSituation situation;

    @SearchableField(field = "createdDateTime")
    private Period createdDateTimePeriod;

}
