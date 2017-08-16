package br.com.unopay.api.bacen.model;

import br.com.unopay.api.model.Updatable;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import static br.com.unopay.api.uaa.exception.Errors.LARGE_PAYMENT_RULE_GROUP_NAME;
import static br.com.unopay.api.uaa.exception.Errors.MAXIMUM_PAYMENT_RULE_GROUP_VALUE_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.MINIMUM_PAYMENT_RULE_GROUP_VALUE_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_RULE_GROUP_CODE_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_RULE_GROUP_NAME_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.SHORT_PAYMENT_RULE_GROUP_NAME;
import static br.com.unopay.api.uaa.exception.Errors.USER_RELATIONSHIP_REQUIRED;
import static javax.persistence.EnumType.STRING;

@Data
@Entity
@EqualsAndHashCode(of = {"code", "id", "name"})
@Table(name = "payment_rule_group")
public class PaymentRuleGroup implements Serializable, Updatable {

    public static final long serialVersionUID = 1L;

    private static final int MAX = 50;
    private static final int MIN = 3;

    public PaymentRuleGroup(){}

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

    @NotNull(groups = {Create.class, Update.class})
    @ManyToOne
    @JoinColumn(name="institution_id")
    @JsonView({Views.Public.class})
    private Institution institution;

    @Min(0)
    @Column(name = "minimum_credit_insertion")
    @JsonView({Views.Public.class,Views.List.class})
    private BigDecimal minimumCreditInsertion;

    @Min(0)
    @Column(name = "maximum_credit_insertion")
    @JsonView({Views.Public.class,Views.List.class})
    private BigDecimal maximumCreditInsertion;


    public void validate(){
        if (getName() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(PAYMENT_RULE_GROUP_NAME_REQUIRED);
        }
        if (code == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(PAYMENT_RULE_GROUP_CODE_REQUIRED);
        }
        if (userRelationship == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(USER_RELATIONSHIP_REQUIRED);
        }
        if (getName().length() > MAX) {
            throw UnovationExceptions.unprocessableEntity().withErrors(LARGE_PAYMENT_RULE_GROUP_NAME);
        }
        if (getName().length() < MIN) {
            throw UnovationExceptions.unprocessableEntity().withErrors(SHORT_PAYMENT_RULE_GROUP_NAME);
        }
        if(minimumCreditInsertion == null || minimumCreditInsertion.compareTo(BigDecimal.ZERO) == 0){
            throw UnovationExceptions.unprocessableEntity().withErrors(MINIMUM_PAYMENT_RULE_GROUP_VALUE_REQUIRED);
        }
        if(maximumCreditInsertion == null || maximumCreditInsertion.compareTo(BigDecimal.ZERO) == 0){
            throw UnovationExceptions.unprocessableEntity().withErrors(MAXIMUM_PAYMENT_RULE_GROUP_VALUE_REQUIRED);
        }
    }
}
