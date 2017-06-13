package br.com.unopay.api.model.filter;

import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.model.ContractSituation;
import br.com.unopay.api.model.Period;
import br.com.unopay.api.repository.filter.SearchableField;
import java.io.Serializable;
import java.util.Set;
import lombok.Data;
import lombok.ToString;

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
    private Set<ServiceType> serviceType;

    @SearchableField
    private ContractSituation situation;

    @SearchableField(field = "begin")
    private Period beginPeriod;

    @SearchableField(field = "end")
    private Period endPeriod;
    

}
