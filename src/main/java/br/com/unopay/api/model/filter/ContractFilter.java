package br.com.unopay.api.model.filter;

import br.com.unopay.api.model.ContractSituation;
import br.com.unopay.api.model.Period;
import br.com.unopay.api.repository.filter.SearchableField;

import java.io.Serializable;

public class ContractFilter  implements Serializable {

    public static final Long serialVersionUID = 1L;

    @SearchableField
    private String code;

    @SearchableField(field = "product.id")
    private String product;

    @SearchableField(field = "hirer.id")
    private String hirer;

    @SearchableField(field = "contractor.id")
    private String contractor;

    @SearchableField
    private ContractSituation situation;

    @SearchableField(field = "begin")
    private Period beginPeriod;

    @SearchableField(field = "end")
    private Period endPeriod;
    

}
