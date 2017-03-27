package br.com.unopay.api.bacen.model;

import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

import static br.com.unopay.api.uaa.exception.Errors.*;
import static javax.persistence.EnumType.STRING;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "payment_rule_group")
public class PaymentRuleGroup implements Serializable {

    private static final int MAX = 50;
    private static final int MIN = 3;

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @NotNull(groups = Create.class)
    @Column(name="code", unique = true)
    @JsonView({Views.Public.class,Views.List.class})
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

    public void validate(){
        if (getName() == null) throw UnovationExceptions.unprocessableEntity().withErrors(PAYMENT_RULE_GROUP_NAME_REQUIRED);
        if (code == null) throw UnovationExceptions.unprocessableEntity().withErrors(PAYMENT_RULE_GROUP_CODE_REQUIRED);
        if (userRelationship == null) throw UnovationExceptions.unprocessableEntity().withErrors(USER_RELATIONSHIP_REQUIRED);
        if (getName().length() > MAX) throw UnovationExceptions.unprocessableEntity().withErrors(LARGE_PAYMENT_RULE_GROUP_NAME);
        if (getName().length() < MIN) throw UnovationExceptions.unprocessableEntity().withErrors(SHORT_PAYMENT_RULE_GROUP_NAME);
    }}
