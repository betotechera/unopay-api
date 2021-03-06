package br.com.unopay.api.credit.model.filter;

import br.com.unopay.api.credit.model.CreditSituation;
import br.com.unopay.bootcommons.model.Period;
import br.com.unopay.bootcommons.repository.filter.SearchableField;
import java.io.Serializable;
import lombok.Data;

@Data
public class CreditFilter  implements Serializable {

    public static final long serialVersionUID = 1L;

    CreditFilter(){}

    @SearchableField(field = "createdDateTime")
    private Period createdDateTimePeriod;

    @SearchableField
    private CreditSituation situation;


    @SearchableField(field = "hirer.person.document.number")
    private String hirerDocument;

}
