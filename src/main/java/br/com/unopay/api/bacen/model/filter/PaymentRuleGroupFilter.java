package br.com.unopay.api.bacen.model.filter;

import br.com.unopay.api.bacen.model.UserRelationship;
import br.com.unopay.bootcommons.repository.filter.SearchableField;
import java.io.Serializable;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PaymentRuleGroupFilter implements Serializable {

    public static final Long serialVersionUID = 1L;

    @SearchableField
    private String code;

    @SearchableField
    private String name;

    @SearchableField
    private UserRelationship userRelationship;

    @SearchableField(field = "institution.person.document.number")
    private String institution;

    public PaymentRuleGroupFilter() {}
}
