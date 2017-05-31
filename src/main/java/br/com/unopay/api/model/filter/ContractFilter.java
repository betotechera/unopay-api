package br.com.unopay.api.model.filter;

import br.com.unopay.api.model.ContractSituation;
import br.com.unopay.api.model.Period;
import br.com.unopay.api.repository.filter.SearchableField;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class ContractFilter  implements Serializable {

    public static final Long serialVersionUID = 1L;

    public ContractFilter(){}

    @SearchableField
    private String code;

    @SearchableField
    private String name;

    @SearchableField(field = "product.code")
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
