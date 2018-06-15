package br.com.unopay.api.model.filter;

import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.model.ContractSituation;
import br.com.unopay.bootcommons.model.Period;
import br.com.unopay.bootcommons.repository.filter.SearchableField;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ContractFilter  implements Serializable {

    public static final Long serialVersionUID = 1L;

    public ContractFilter(){}

    @SearchableField
    private Long code;

    @SearchableField(field = "code")
    private List<Long> inCodes;

    @SearchableField
    private String name;

    @SearchableField(field = "product.code")
    private String product;

    @SearchableField(field = "product.issuer.id")
    private Set<String> issuers;

    @SearchableField(field = "hirer.id")
    private String hirer;

    @SearchableField(field = "hirer.person.document.number")
    private String hirerDocumentNumber;

    @SearchableField(field = "contractor.id")
    private String contractor;

    @SearchableField(field = "contractor.person.document.number")
    private String contractorDocumentNumber;

    @SearchableField
    private Set<ServiceType> serviceTypes;

    @SearchableField
    private ContractSituation situation;

    @SearchableField(field = "begin")
    private Period beginPeriod;

    @SearchableField(field = "end")
    private Period endPeriod;

    @SearchableField
    private Period createdDateTime;
    

}
