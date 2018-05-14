package br.com.unopay.api.bacen.model;

import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;

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
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static br.com.unopay.api.uaa.exception.Errors.MOVEMENT_ACCOUNT_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_ACCOUNT_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.PERSON_REQUIRED;

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
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @Column(name="id")
    private String id;

    @Valid
    @ManyToOne
    @JoinColumn(name="person_id")
    @NotNull(groups = {Create.class, Update.class})
    private Person person;


    @BatchSize(size = 10)
    @OneToMany(fetch = FetchType.EAGER)
    @JsonView({Views.Issuer.Detail.class})
    @JoinTable(name = "payment_rule_group_issuer",
            joinColumns = { @JoinColumn(name = "issuer_id") },
            inverseJoinColumns = { @JoinColumn(name = "payment_rule_group_id") })
    private Set<PaymentRuleGroup> paymentRuleGroups;

    @Column(name = "fee")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Issuer.Detail.class})
    private Double fee;

    @DecimalMin("0.0")
    @Column(name = "credit_card_fee")
    @JsonView({Views.Issuer.Detail.class})
    private BigDecimal creditCardFee;

    @Valid
    @NotNull(groups = {Create.class, Update.class})
    @JoinColumn(name="payment_account_id")
    @JsonView({Views.BankAccount.class, Views.Issuer.Detail.class})
    @OneToOne
    private PaymentBankAccount paymentAccount;

    @Valid
    @NotNull(groups = {Create.class, Update.class})
    @OneToOne
    @JoinColumn(name="movement_account_id")
    @JsonView({Views.BankAccount.class})
    private BankAccount movementAccount;

    @Column(name = "financier_mail_for_remittance")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Issuer.Detail.class})
    private String financierMailForRemittance;

    @Size(max = 6)
    @Column(name = "bin")
    @JsonView({Views.Issuer.Detail.class})
    @NotNull(groups = {Create.class, Update.class})
    private String bin;

    @Column(name = "logo_uri")
    @JsonView({Views.Issuer.Detail.class, Views.Product.List.class})
    private String logoUri;

    @Column(name = "background_color")
    @JsonView({Views.Issuer.Detail.class, Views.Product.List.class})
    private String backgroundColor;

    @Column(name = "text_color")
    @JsonView({Views.Issuer.Detail.class, Views.Product.List.class})
    private String textColor;

    @Column(name = "authorize_service_without_contractor_password")
    @JsonView({Views.Issuer.List.class})
    private Boolean authorizeServiceWithoutContractorPassword;

    @Version
    @JsonIgnore
    Long version;

    public void updateMe(Issuer other){
        setFee(other.getFee());
        setMovementAccount(other.getMovementAccount());
        setPaymentAccount(other.getPaymentAccount());
        setPaymentRuleGroups(other.getPaymentRuleGroups());
        updateServicePasswordRequired(other.authorizeServiceWithoutContractorPassword);
        this.bin = other.getBin();
        this.creditCardFee = other.getCreditCardFee();
        this.financierMailForRemittance = other.getFinancierMailForRemittance();
        this.logoUri = other.logoUri;
        this.backgroundColor = other.backgroundColor;
        this.textColor = other.textColor;
        person.update(other.getPerson(), (x) -> x.updateForIssuer(x));
    }

    public void setMeUp() {
        if(authorizeServiceWithoutContractorPassword == null) {
            authorizeServiceWithoutContractorPassword = false;
        }
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

    public Integer paymentBankCode(){
        return  this.paymentAccount.getBankAccount().bacenCode();
    }

    @JsonIgnore
    public List<String> getPaymentRuleGroupIds() {
        return paymentRuleGroups.stream().map(PaymentRuleGroup::getId).collect(Collectors.toList());
    }

    public String depositPeriodPattern(){
        return getPaymentAccount().getDepositPeriod().getPattern();
    }

    public String documentNumber(){
        return person.documentNumber();
    }

    @JsonIgnore
    public boolean hasPaymentRuleGroup(){
        return getPaymentRuleGroups() != null && !getPaymentRuleGroups().isEmpty();
    }

    public void updateServicePasswordRequired(Boolean isRequired) {
        if(isRequired != null) {
            this.authorizeServiceWithoutContractorPassword = isRequired;
        }
    }

    public String personShortName() {
        if (getPerson() != null) {
            return getPerson().getShortName();
        }
        return null;
    }
}
