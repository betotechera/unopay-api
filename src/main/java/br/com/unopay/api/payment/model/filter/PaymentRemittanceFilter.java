package br.com.unopay.api.payment.model.filter;


import br.com.unopay.api.repository.filter.SearchableField;
import lombok.Data;

@Data
public class PaymentRemittanceFilter {

    @SearchableField(field = "remittanceItems.establishment.id")
    private String establishment;

    @SearchableField(field = "remittanceItems.establishment.accreditedNetwork.id")
    private String accreditedNetwork;

    @SearchableField(field = "issuer.id")
    private String issuer;

}
