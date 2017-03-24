package br.com.unopay.api.bacen.model;

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
@Entity
@EqualsAndHashCode
@Table(name = "payment_rule_group")
public class PaymentRuleGroup {

    @Id
    @Column(name="id")
    @NotNull(groups = Update.class)
    @GeneratedValue(generator="system-uuid")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @NotNull(groups = Create.class)
    @Column(name="code", unique = true)
    @JsonView({Views.Public.class,Views.List.class})
    @Size(min=2, max = 50, groups = {Create.class, Update.class})
    private String code;

    @Column(name="name")
    @NotNull(groups = Create.class)
    @JsonView({Views.Public.class,Views.List.class})
    @Size(min=2, max = 50, groups = {Create.class, Update.class})
    private String name;

    @Enumerated(STRING)
    @Column(name="purpose")
    @JsonView({Views.Public.class,Views.List.class})
    private Purpose purpose;

    @Enumerated(STRING)
    @Column(name="scope")
    @JsonView({Views.Public.class,Views.List.class})
    private Scope scope;

    @Enumerated(STRING)
    @NotNull(groups = Create.class)
    @Column(name="user_relationship")
    @JsonView({Views.Public.class,Views.List.class})
    private UserRelationship userRelationship;

    public void updateModel(PaymentRuleGroup ruleGroup) {
        if(ruleGroup.getCode() !=null)
            this.code = ruleGroup.getCode();
        if(ruleGroup.getName() !=null)
            this.name = ruleGroup.getName();
        if(ruleGroup.getPurpose() !=null)
            this.purpose = ruleGroup.getPurpose();
        if(ruleGroup.getUserRelationship() !=null)
            this.userRelationship = ruleGroup.getUserRelationship();
    }
}
