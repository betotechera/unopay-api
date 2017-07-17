package br.com.unopay.api.model.filter;

import br.com.unopay.api.model.Period;
import br.com.unopay.api.model.TransactionSituation;
import br.com.unopay.api.repository.filter.SearchableField;
import java.io.Serializable;
import lombok.Data;

@Data
public class ServiceAuthorizeFilter implements Serializable {

    public static final Long serialVersionUID = 1L;

    ServiceAuthorizeFilter(){}

    @SearchableField(field = "authorizationDateTime")
    private Period authorizationDateTime;

    @SearchableField
    private TransactionSituation situation;

    @SearchableField
    private String authorizationNumber;


}
