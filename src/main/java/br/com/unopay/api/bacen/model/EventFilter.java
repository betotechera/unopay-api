package br.com.unopay.api.bacen.model;

import br.com.unopay.api.repository.SearchableField;
import lombok.Data;

import java.io.Serializable;

@Data
public class EventFilter  implements Serializable {

    public static final Long serialVersionUID = 1L;

    @SearchableField(field = "service.name")
    private String serviceName;

    @SearchableField(field = "paymentRuleGroups.name")
    private String paymentRuleGroupName;

}
