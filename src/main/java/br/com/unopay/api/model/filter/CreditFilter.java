package br.com.unopay.api.model.filter;

import br.com.unopay.api.model.CreditSituation;
import br.com.unopay.api.model.Period;
import br.com.unopay.api.repository.filter.SearchableField;
import lombok.Data;

import java.io.Serializable;

@Data
public class CreditFilter  implements Serializable {

    public static final long serialVersionUID = 1L;

    CreditFilter(){}

    @SearchableField(field = "createdDateTime")
    private Period createdDateTimePeriod;

    @SearchableField
    private CreditSituation situation;


    @SearchableField
    private String hirerDocument;

}
