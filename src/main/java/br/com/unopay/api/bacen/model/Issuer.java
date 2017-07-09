package br.com.unopay.api.bacen.model;

import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import static br.com.unopay.api.uaa.exception.Errors.MOVEMENT_ACCOUNT_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_ACCOUNT_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.PERSON_REQUIRED;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@ToString(exclude = "paymentRuleGroups")
@EqualsAndHashCode(exclude = "paymentRuleGroups")
@Table(name = "issuer")
public class Issuer implements Serializable{

    public static final long serialVersionUID = 1L;

    public Issuer(){}

    @Id
    @GeneratedValue(generator="system-uuid")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @Column(name="id")
    private String id;

    @Valid
    @NotNull(groups = {Create.class, Update.class})
    @ManyToOne
    @JsonView({Views.Public.class,Views.List.class})
    @JoinColumn(name="person_id")
    private Person person;


    @BatchSize(size = 10)
    @OneToMany(fetch = FetchType.EAGER)
    @JsonView({Views.Public.class,Views.List.class})
    @JoinTable(name = "payment_rule_group_issuer",
            joinColumns = { @JoinColumn(name = "issuer_id") },
            inverseJoinColumns = { @JoinColumn(name = "payment_rule_group_id") })
    private Set<PaymentRuleGroup> paymentRuleGroups;


    @Column(name = "fee")
    @NotNull(groups = {Create.class, Update.class})
    private Double fee;

    @Valid
    @NotNull(groups = {Create.class, Update.class})
    @JoinColumn(name="payment_account_id")
    @JsonView({Views.Public.class,Views.List.class})
    @OneToOne
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
        setFee(other.getFee());
        setMovementAccount(other.getMovementAccount());
        setPaymentAccount(other.getPaymentAccount());
        setPaymentRuleGroups(other.getPaymentRuleGroups());
        person.update(other.getPerson(), (x) -> x.updateForIssuer(x));
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

    public String depositPeriodPattern(){
        return getPaymentAccount().getDepositPeriod().getPattern();
    }

    @JsonIgnore
    public boolean hasPaymentRuleGroup(){
        return getPaymentRuleGroups() != null && !getPaymentRuleGroups().isEmpty();
    }
}
