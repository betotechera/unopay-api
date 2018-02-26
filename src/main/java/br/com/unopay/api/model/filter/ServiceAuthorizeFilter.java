package br.com.unopay.api.model.filter;

import br.com.unopay.api.model.AuthorizationSituation;
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

    @SearchableField(field = "establishment.person.document.number")
    private String establishmentDocument;

    @SearchableField(field = "establishment.network.id")
    private String network;

    @SearchableField(field = "contractor.id")
    private String contractor;

    @SearchableField(field = "contractor.person.document.number")
    private String contractorDocument;

    @SearchableField(field = "contract.hirer.id")
    private String hirer;

    @SearchableField(field = "authorizationDateTime")
    private Period authorizationDateTime;

    @SearchableField
    private AuthorizationSituation situation;

    @SearchableField
    private String authorizationNumber;


}
