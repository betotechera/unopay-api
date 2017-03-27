package br.com.unopay.api.bacen.model;

import br.com.unopay.api.repository.SearchableField;
import lombok.Data;

@Data
public class InstitutionFilter {

    @SearchableField(field = "person.document.number")
    private String documentNumber;

    @SearchableField(field = "person.legalPersonDetail.fantasyName")
    private String fantasyName;

    @SearchableField(field = "paymentRuleGroup.name")
    private String paymentRuleGroupName;

}
