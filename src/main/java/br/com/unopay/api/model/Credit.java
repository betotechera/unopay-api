package br.com.unopay.api.model;

import br.com.unopay.api.bacen.model.PaymentRuleGroup;
import br.com.unopay.api.bacen.model.ServiceType;
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
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.GenericGenerator;

import static br.com.unopay.api.model.CreditInsertionType.DIRECT_DEBIT;
import static br.com.unopay.api.uaa.exception.Errors.CREDIT_ALREADY_CANCELED;
import static br.com.unopay.api.uaa.exception.Errors.MAXIMUM_PAYMENT_RULE_GROUP_VALUE_NOT_MET;
import static br.com.unopay.api.uaa.exception.Errors.MINIMUM_CREDIT_VALUE_NOT_MET;
import static br.com.unopay.api.uaa.exception.Errors.MINIMUM_PAYMENT_RULE_GROUP_VALUE_NOT_MET;
import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_RULE_GROUP_REQUIRED;

@Data
@Entity
@EqualsAndHashCode(exclude = {"paymentRuleGroup", "product"})
@ToString(exclude = {"paymentRuleGroup", "product"})
@Table(name = "credit")
public class Credit implements Serializable, Updatable {

    public Credit(){}

    public static final long serialVersionUID = 1L;

    @Id
    @Column(name="id")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @GeneratedValue(generator="system-uuid")
    private String id;

    @ManyToOne
    @JoinColumn(name="product_id")
    @JsonView({Views.List.class})
    private Product product;

    @ManyToOne
    @JoinColumn(name="payment_rule_group_id")
    @JsonView({Views.Public.class,Views.List.class})
    private PaymentRuleGroup paymentRuleGroup;

    @Column(name = "hirer_document")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private String hirerDocument;

    @Column(name = "issuer_document")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private String issuerDocument;

    @Column(name = "service_type")
    @Enumerated(EnumType.STRING)
    @JsonView({Views.Public.class,Views.List.class})
    private ServiceType serviceType;

    @Column(name = "credit_insertion_type")
    @Enumerated(EnumType.STRING)
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private CreditInsertionType creditInsertionType;

    @Column(name = "credit_number")
    @JsonView({Views.Public.class,Views.List.class})
    private Long creditNumber;

    @Column(name = "created_date_time")
    @JsonView({Views.Public.class,Views.List.class})
    private Date createdDateTime;

    @Column(name = "value")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private BigDecimal value;

    @Column(name = "situation")
    @Enumerated(EnumType.STRING)
    @JsonView({Views.Public.class,Views.List.class})
    private CreditSituation situation;

    @Column(name = "credit_source")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private String creditSource;

    @Column(name = "cnab_id")
    @JsonView({Views.Public.class,Views.List.class})
    private String cnabId;

    @Column(name = "available_value")
    @JsonView({Views.Public.class,Views.List.class})
    private BigDecimal availableValue;

    @Column(name = "blocked_value")
    @JsonView({Views.Public.class,Views.List.class})
    private BigDecimal blockedValue;

    @Version
    @JsonIgnore
    private Integer version;

    public boolean withProduct(){
        return product != null;
    }

    public void validateCreditValue() {
        if(withProduct()){
            getProduct().validateCreditInsertionType(this.creditInsertionType);
            if(value.compareTo(paymentRuleGroup.getMinimumCreditInsertion()) == -1){
                throw UnovationExceptions.unprocessableEntity().withErrors(MINIMUM_PAYMENT_RULE_GROUP_VALUE_NOT_MET);
            }
            if(value.compareTo(paymentRuleGroup.getMaximumCreditInsertion()) == 1){
                throw UnovationExceptions.unprocessableEntity().withErrors(MAXIMUM_PAYMENT_RULE_GROUP_VALUE_NOT_MET);
            }
            if(value.compareTo(paymentRuleGroup.getMaximumCreditInsertion()) == 1){
                throw UnovationExceptions.unprocessableEntity().withErrors(MAXIMUM_PAYMENT_RULE_GROUP_VALUE_NOT_MET);
            }
            if(!getProduct().getCreditInsertionTypes().contains(this.creditInsertionType)) {
                throw UnovationExceptions.unprocessableEntity().withErrors(Errors.CREDIT_INSERTION_TYPE_NOT_IN_PRODUCT);
            }
        }else
        if(value.compareTo(new BigDecimal(0)) == 0){
            throw UnovationExceptions.unprocessableEntity().withErrors(MINIMUM_CREDIT_VALUE_NOT_MET);
        }
        if(getPaymentRuleGroup() == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(PAYMENT_RULE_GROUP_REQUIRED);
        }

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
        if(isDirectDebit()) {
            situation = CreditSituation.PROCESSING;
        }
        if(creditInsertionType.isPaymentProcessedByClient()){
            situation = CreditSituation.CONFIRMED;
        }
    }

    public boolean isDirectDebit() {
        return DIRECT_DEBIT.equals(creditInsertionType);
    }

    public void defineCreditInsertionType(String creditInsertionType){
        this.creditInsertionType = CreditInsertionType.valueOf(creditInsertionType);
    }

    public void defineAvailableValue(){
        if(isDirectDebit()){
            availableValue = BigDecimal.ZERO;
            return;
        }
        availableValue = this.value;
    }

    public void defineBlockedValue(){
        if(creditInsertionType.isPaymentProcessedByClient()){
            blockedValue = BigDecimal.ZERO;
            return;
        }
        blockedValue = this.value;
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
    public String getPaymentRuleGroupId(){
        if(paymentRuleGroup != null){
            return paymentRuleGroup.getId();
        }
        return null;
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
}
