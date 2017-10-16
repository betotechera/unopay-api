package br.com.unopay.api.bacen.model.filter;

import br.com.unopay.bootcommons.repository.filter.SearchableField;
import java.io.Serializable;
import lombok.Data;

@Data
public class InstitutionFilter implements Serializable {

    public static final Long serialVersionUID = 1L;

    @SearchableField(field = "person.document.number")
    private String documentNumber;

    @SearchableField(field = "person.name")
    private String name;

    @SearchableField(field = "paymentRuleGroups.name")
    private String paymentRuleGroupName;

}
