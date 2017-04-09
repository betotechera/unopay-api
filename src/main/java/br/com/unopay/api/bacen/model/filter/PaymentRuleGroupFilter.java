package br.com.unopay.api.bacen.model.filter;

import br.com.unopay.api.bacen.model.UserRelationship;
import br.com.unopay.api.repository.filter.SearchableField;
import lombok.Data;

import java.io.Serializable;

@Data
public class PaymentRuleGroupFilter implements Serializable {

    public static final Long serialVersionUID = 1L;

    @SearchableField
    private String code;

    @SearchableField
    private String name;

    @SearchableField
    private UserRelationship userRelationship;

    public PaymentRuleGroupFilter() {}
}
