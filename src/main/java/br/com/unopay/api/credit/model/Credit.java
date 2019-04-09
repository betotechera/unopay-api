package br.com.unopay.api.credit.model;

import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.PaymentRuleGroup;
import br.com.unopay.api.network.model.ServiceType;
import br.com.unopay.api.billing.boleto.model.TicketPaymentSource;
import br.com.unopay.api.billing.creditcard.model.PaymentRequest;
import br.com.unopay.api.billing.creditcard.model.TransactionStatus;
import br.com.unopay.api.market.model.NegotiationBilling;
import br.com.unopay.api.model.Billable;
import br.com.unopay.api.model.ContractOrigin;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.Product;
import br.com.unopay.api.model.Updatable;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.util.Rounder;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.GenericGenerator;

import static br.com.unopay.api.billing.creditcard.model.TransactionStatus.CANCELED;
import static br.com.unopay.api.billing.creditcard.model.TransactionStatus.CANCEL_PENDING;
import static br.com.unopay.api.billing.creditcard.model.TransactionStatus.CAPTURED;
import static br.com.unopay.api.billing.creditcard.model.TransactionStatus.CAPTURE_RECEIVED;
import static br.com.unopay.api.billing.creditcard.model.TransactionStatus.DENIED;
import static br.com.unopay.api.billing.creditcard.model.TransactionStatus.REFUND;
import static br.com.unopay.api.uaa.exception.Errors.CREDIT_ALREADY_CANCELED;
import static br.com.unopay.api.uaa.exception.Errors.MAXIMUM_PAYMENT_RULE_GROUP_VALUE_NOT_MET;
import static br.com.unopay.api.uaa.exception.Errors.MINIMUM_PAYMENT_RULE_GROUP_VALUE_NOT_MET;

@Data
@Entity
@EqualsAndHashCode(exclude = {"paymentRuleGroup", "product"})
@ToString(exclude = {"paymentRuleGroup", "product"})
@Table(name = "credit")
public class Credit implements Updatable, Billable, Serializable {

    private static final long serialVersionUID = -6537120024031518385L;

    public Credit(){}

    public Credit(NegotiationBilling billing){
        this.value = billing.getCreditValue();
        this.hirer = billing.hirer();
        this.product = billing.product();
        this.creditSource = ContractOrigin.APPLICATION.name();
        this.creditInsertionType = CreditInsertionType.BOLETO;
        this.billable = false;

    }

    @Id
    @Column(name="id")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @GeneratedValue(generator="system-uuid")
    private String id;

    @ManyToOne
    @JoinColumn(name="product_id")
    @JsonView({Views.Credit.List.class})
    private Product product;

    @ManyToOne
    @JoinColumn(name="payment_rule_group_id")
    @JsonView({Views.Credit.Detail.class})
    private PaymentRuleGroup paymentRuleGroup;

    @ManyToOne
    @JoinColumn(name = "hirer_id")
    @JsonView({Views.Credit.List.class})
    private Hirer hirer;

    @ManyToOne
    @JoinColumn(name = "issuer_id")
    @JsonView({Views.Credit.Detail.class})
    private Issuer issuer;

    @Column(name = "service_type")
    @Enumerated(EnumType.STRING)
    @JsonView({Views.Credit.List.class})
    private ServiceType serviceType;

    @Column(name = "credit_insertion_type")
    @Enumerated(EnumType.STRING)
    @JsonView({Views.Credit.Detail.class})
    @NotNull(groups = {Create.class, Update.class})
    private CreditInsertionType creditInsertionType;

    @Column(name = "credit_number")
    @JsonView({Views.Credit.Detail.class})
    private Long creditNumber;

    @Column(name = "created_date_time")
    @JsonView({Views.Credit.List.class})
    private Date createdDateTime;

    @Column(name = "value")
    @JsonView({Views.Credit.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private BigDecimal value;

    @Column(name = "situation")
    @Enumerated(EnumType.STRING)
    @JsonView({Views.Credit.List.class})
    private CreditSituation situation;

    @Column(name = "credit_source")
    @JsonView({Views.Credit.Detail.class})
    @NotNull(groups = {Create.class, Update.class})
    private String creditSource;

    @Column(name = "cnab_id")
    @JsonView({Views.Credit.Detail.class})
    private String cnabId;

    @Column(name = "available_value")
    @JsonView({Views.Credit.Detail.class})
    private BigDecimal availableValue;

    @Column(name = "blocked_value")
    @JsonView({Views.Credit.Detail.class})
    private BigDecimal blockedValue;

    @Valid
    @Transient
    private PaymentRequest paymentRequest;

    @Transient
    private boolean billable = true;

    @Version
    @JsonIgnore
    private Integer version;

    public boolean withProduct(){
        return product != null;
    }

    public boolean withPaymentRuleGroup(){
        return paymentRuleGroup != null;
    }

    public void validateCreditValue() {
        if(withProduct()){
            getProduct().validateCreditInsertionType(this.creditInsertionType);
            if(!getProduct().getCreditInsertionTypes().contains(this.creditInsertionType)) {
                throw UnovationExceptions.unprocessableEntity().withErrors(Errors.CREDIT_INSERTION_TYPE_NOT_IN_PRODUCT);
            }
        }
        if(paymentRuleGroup.getMinimumCreditInsertion() != null &&
                value.compareTo(paymentRuleGroup.getMinimumCreditInsertion()) == -1){
            throw UnovationExceptions.unprocessableEntity().withErrors(MINIMUM_PAYMENT_RULE_GROUP_VALUE_NOT_MET);
        }
        if(paymentRuleGroup.getMaximumCreditInsertion() != null &&
                value.compareTo(paymentRuleGroup.getMaximumCreditInsertion()) == 1){
            throw UnovationExceptions.unprocessableEntity().withErrors(MAXIMUM_PAYMENT_RULE_GROUP_VALUE_NOT_MET);
        }

    }

    public boolean valueIs(BigDecimal other){
        return value.compareTo(other) == 0;
    }

    public void setupMyCreate(){
        defineSituation();
        createdDateTime = new Date();
        if(this.withProduct()){
            paymentRuleGroup = product.getPaymentRuleGroup();
        }
        defineAvailableValue();
        defineBlockedValue();
    }

    private void defineSituation() {
        situation = CreditSituation.PROCESSING;
    }

    public boolean isDirectDebit() {
        return CreditInsertionType.DIRECT_DEBIT.equals(creditInsertionType);
    }

    public boolean isCreditCard() {
        return CreditInsertionType.CREDIT_CARD.equals(creditInsertionType);
    }

    public boolean isBoleto() {
        return CreditInsertionType.BOLETO.equals(creditInsertionType);
    }


    public void defineAvailableValue(){
        if(!confirmedSituation()) {
            availableValue = BigDecimal.ZERO;
            return;
        }
        availableValue = value;
    }

    public void defineBlockedValue(){
        if(!confirmedSituation()) {
            blockedValue = this.value;
            return;
        }
        blockedValue = BigDecimal.ZERO;
    }

    public BigDecimal getAvailableValue(){
        if(availableValue != null) {
            return Rounder.round(availableValue);
        }
        return Rounder.zero();
    }

    public BigDecimal getBlockedValue(){
        if(blockedValue != null) {
            return Rounder.round(blockedValue);
        }
        return Rounder.zero();
    }

    public void defineCreditNumber(Long lastCreditNumber) {
        if(lastCreditNumber == null){
            creditNumber = 1L;
            return;
        }
        creditNumber = lastCreditNumber + 1;
    }

    @JsonIgnore
    public String getProductId(){
        if(product!= null){
            return product.getId();
        }
        return null;
    }

    @JsonIgnore
    public String issuerId(){
        if(issuer != null){
            return issuer.getId();
        }
        return null;
    }

    @JsonIgnore
    public String hirerId(){
        if(hirer != null){
            return hirer.getId();
        }
        return null;
    }

    @JsonIgnore
    public String getPaymentRuleGroupId(){
        if(paymentRuleGroup != null){
            return paymentRuleGroup.getId();
        }
        return null;
    }

    @JsonIgnore
    public String getBillingMail() {
        if(this.getHirer() != null){
            return this.getHirer().getFinancierMail();
        }
        return null;
    }

    @Override
    public TicketPaymentSource getPaymentSource() {
        return TicketPaymentSource.HIRER_CREDIT;
    }

    public Optional<CreditPaymentAccount> filterLastByProductAndService(List<CreditPaymentAccount> creditPayment) {
        return creditPayment.stream()
                .filter(c -> Objects.equals(c.getProductId(), getProductId()) && c.getServiceType() == getServiceType())
                .reduce((first, last) -> last);
    }

    public void cancel(){
        if(CreditSituation.CANCELED.equals(situation)){
            throw UnovationExceptions.unprocessableEntity().withErrors(CREDIT_ALREADY_CANCELED);
        }
        this.situation = CreditSituation.CANCELED;
    }

    public void setCreatedDateTime(Date dateTime){
        this.createdDateTime = ObjectUtils.clone(dateTime);
    }

    public Date getCreatedDateTime(){
        return ObjectUtils.clone(this.createdDateTime);
    }

    private boolean confirmedSituation() {
        return CreditSituation.CONFIRMED.equals(situation);
    }

    @JsonIgnore
    @Override
    public Person getPayer() {
        return getHirer().getPerson();
    }

    @JsonIgnore
    @Override
    public String getNumber() {
        return String.valueOf(getCreditNumber());
    }

    @JsonIgnore
    @Override
    public Date getCreateDateTime() {
        return getCreatedDateTime();
    }

    public void defineStatus(TransactionStatus transactionStatus) {
        if(Arrays.asList(CANCELED, CANCEL_PENDING, REFUND).contains(transactionStatus)){
            this.situation = CreditSituation.CANCELED;
            return;
        }
        if(Arrays.asList(CAPTURED, CAPTURE_RECEIVED).contains(transactionStatus)){
            this.situation = CreditSituation.AVAILABLE;
            return;
        }
        if(DENIED.equals(transactionStatus)){
            this.situation = CreditSituation.CANCELED;
            return;
        }
        this.situation = CreditSituation.PROCESSING;

    }

    public boolean confirmed() {
        return CreditSituation.CONFIRMED.equals(situation);
    }

    public boolean billable() {
        return billable;
    }
}
