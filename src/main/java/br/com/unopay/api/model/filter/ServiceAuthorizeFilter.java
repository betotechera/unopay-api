package br.com.unopay.api.model.filter;

import br.com.unopay.api.model.TransactionSituation;
import br.com.unopay.bootcommons.model.Period;
import br.com.unopay.bootcommons.repository.filter.SearchableField;
import java.io.Serializable;
import lombok.Data;

@Data
public class ServiceAuthorizeFilter implements Serializable {

    public static final Long serialVersionUID = 1L;

    ServiceAuthorizeFilter(){}

    @SearchableField(field = "establishment.id")
    private String establishment;

    @SearchableField(field = "establishment.network.id")
    private String network;

    @SearchableField(field = "contractor.id")
    private String contractor;

    @SearchableField(field = "contract.hirer.id")
    private String hirer;

    @SearchableField(field = "authorizationDateTime")
    private Period authorizationDateTime;

    @SearchableField
    private TransactionSituation situation;

    @SearchableField
    private String authorizationNumber;


}
