package br.com.unopay.api.bacen.model;

import br.com.unopay.api.repository.SearchableField;
import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static javax.persistence.EnumType.STRING;

@Data
public class PaymentRuleGroupFilter {
    @SearchableField
    private String code;

    @SearchableField
    private String name;

    @SearchableField
    private UserRelationship userRelationship;
}
