package br.com.unopay.api.model.filter;

import br.com.unopay.api.repository.filter.SearchableField;

import java.io.Serializable;

public class ProductFilter implements Serializable {

    public static final long serialVersionUID = 1L;

    @SearchableField
    private String code;

    @SearchableField
    private String name;

    @SearchableField(field = "issuer.name")
    private String issuerName;

    @SearchableField
    private String serviceType;

    @SearchableField
    private String situation;

    @SearchableField
    private String paymentInstrumentType;

    @SearchableField(field = "type")
    private String productType;

    @SearchableField
    private String creditInsertionType;





}
