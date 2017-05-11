package br.com.unopay.api.model.filter;

import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.model.CreditInsertionType;
import br.com.unopay.api.model.PaymentInstrumentType;
import br.com.unopay.api.model.ProductSituation;
import br.com.unopay.api.model.ProductType;
import br.com.unopay.api.repository.filter.SearchableField;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class ProductFilter implements Serializable {

    public static final long serialVersionUID = 1L;

    ProductFilter(){}

    @SearchableField
    private String code;

    @SearchableField
    private String name;

    @SearchableField(field = "issuer.name")
    private String issuerName;

    @SearchableField
    private ServiceType serviceType;

    @SearchableField
    private ProductSituation situation;

    @SearchableField
    private PaymentInstrumentType paymentInstrumentType;

    @SearchableField
    private ProductType type;

    @SearchableField
    private CreditInsertionType creditInsertionType;


}
