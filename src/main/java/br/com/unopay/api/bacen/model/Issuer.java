package br.com.unopay.api.bacen.model;


import br.com.unopay.api.model.Person;
import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.unopay.api.uaa.exception.Errors.*;

@Data
@Entity
@EqualsAndHashCode(exclude = "paymentRuleGroups")
@Table(name = "issuer")
public class Issuer implements Serializable{

    public static final Long serialVersionUID = 1L;

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @Valid
    @NotNull(groups = {Create.class, Update.class})
    @ManyToOne
    @JoinColumn(name="person_id")
    @JsonView({Views.Public.class,Views.List.class})
    private Person person;


    @BatchSize(size = 10)
    @OneToMany(fetch = FetchType.EAGER)
    @JsonView({Views.Public.class,Views.List.class})
    @JoinTable(name = "payment_rule_group_issuer",
            joinColumns = { @JoinColumn(name = "issuer_id") },
            inverseJoinColumns = { @JoinColumn(name = "payment_rule_group_id") })
    private List<PaymentRuleGroup> paymentRuleGroups;


    @Column(name = "tax")
    @NotNull(groups = {Create.class, Update.class})
    private Double tax;

    @Valid
    @NotNull(groups = {Create.class, Update.class})
    @OneToOne
    @JoinColumn(name="payment_account_id")
    @JsonView({Views.Public.class,Views.List.class})
    private PaymentBankAccount paymentAccount;

    @Valid
    @NotNull(groups = {Create.class, Update.class})
    @OneToOne
    @JoinColumn(name="movement_account_id")
    @JsonView({Views.Public.class,Views.List.class})
    private BankAccount movementAccount;

    @Version
    @JsonIgnore
    Long version;

    public void updateMe(Issuer other){
        setTax(other.getTax());
        setMovementAccount(other.getMovementAccount());
        setPaymentAccount(other.getPaymentAccount());
        setPaymentRuleGroups(other.getPaymentRuleGroups());
        this.person.updateForIssuer(other.getPerson());
    }

    public void validate(){
        if(person == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(PERSON_REQUIRED);
        }
        if(paymentAccount == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(PAYMENT_ACCOUNT_REQUIRED);
        }
        if(movementAccount == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(MOVEMENT_ACCOUNT_REQUIRED);
        }
    }

    @JsonIgnore
    public String getMomentAccountId() {
        if(getMovementAccount() != null){
            return getMovementAccount().getId();
        }
        return  null;
    }

    @JsonIgnore
    public String getPaymentAccountId() {
        if(getPaymentAccount() != null){
            return getPaymentAccount().getId();
        }
        return  null;
    }

    @JsonIgnore
    public List<String> getPaymentRuleGroupIds() {
        return paymentRuleGroups.stream().map(PaymentRuleGroup::getId).collect(Collectors.toList());
    }

    @JsonIgnore
    public boolean hasPaymentRuleGroup(){
        return getPaymentRuleGroups() != null && !getPaymentRuleGroups().isEmpty();
    }
}
