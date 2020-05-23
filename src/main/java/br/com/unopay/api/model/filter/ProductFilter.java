package br.com.unopay.api.model.filter;

import br.com.unopay.api.network.model.ServiceType;
import br.com.unopay.api.credit.model.CreditInsertionType;
import br.com.unopay.api.model.PaymentInstrumentType;
import br.com.unopay.api.model.ProductSituation;
import br.com.unopay.bootcommons.repository.filter.SearchableField;
import java.io.Serializable;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ProductFilter implements Serializable {

    public static final long serialVersionUID = 1L;

    public ProductFilter(){}

    @SearchableField
    private String code;

    @SearchableField
    private String name;

    @SearchableField(field = "issuer.person.document.number")
    private String issuerDocument;

    @SearchableField
    private ServiceType serviceType;

    @SearchableField
    private ProductSituation situation;

    @SearchableField
    private PaymentInstrumentType paymentInstrumentType;

    @SearchableField
    private CreditInsertionType creditInsertionType;

    @SearchableField(field = "partners.id")
    private String partner;

}
