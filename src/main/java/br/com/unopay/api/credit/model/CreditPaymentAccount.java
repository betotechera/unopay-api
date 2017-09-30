package br.com.unopay.api.credit.model;

import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.PaymentRuleGroup;
import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.model.Product;
import br.com.unopay.api.model.Updatable;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.order.model.Order;
import br.com.unopay.api.util.Rounder;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
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
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.GenericGenerator;

import static br.com.unopay.api.uaa.exception.Errors.CREDIT_REQUIRED_WHEN_SUBTRACT_BALANCE;
import static br.com.unopay.api.uaa.exception.Errors.CREDIT_REQUIRED_WHEN_UPDATE_BALANCE;
import static br.com.unopay.api.uaa.exception.Errors.VALUE_GREATER_THEN_AVAILABLE_BALANCE;

@Data
@Entity
@Table(name = "credit_payment_account")
public class CreditPaymentAccount implements Serializable, Updatable {

    public static final long serialVersionUID = 1L;

    public CreditPaymentAccount(){}

    public CreditPaymentAccount(Credit credit){
        if(credit != null) {
            if (credit.withProduct()) {
                this.issuer = credit.getProduct().getIssuer();
            }
            this.product = credit.getProduct();
            this.paymentRuleGroup = credit.getPaymentRuleGroup();
            this.hirerDocument = credit.getHirerDocument();
            this.serviceType = credit.getServiceType();
            this.creditInsertionType = credit.getCreditInsertionType();
            this.creditNumber = credit.getCreditNumber();
            this.value = credit.getValue();
            this.situation = credit.getSituation();
            this.creditSource = credit.getCreditSource();
            this.availableBalance = credit.getAvailableValue();
        }
    }

    public CreditPaymentAccount(Order order){
        if(order != null) {
            this.transactionCreatedDateTime = new Date();
            this.issuer = order.getProduct().getIssuer();
            this.product = order.getProduct();
            this.paymentRuleGroup = order.getProduct().getPaymentRuleGroup();
            this.hirerDocument = order.getContract().getHirer().getDocumentNumber();
            this.creditInsertionType = CreditInsertionType.DIRECT_DEBIT;
            this.creditNumber = 999999999L;
            this.value = order.getValue();
            this.situation = CreditSituation.AVAILABLE;
            this.creditSource = InstrumentCreditSource.CLIENT.name();
            this.availableBalance = order.getValue();
        }
    }

    @Id
    @Column(name="id")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @GeneratedValue(generator="system-uuid")
    private String id;

    @Column(name = "transaction_created_date_time")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.CreditPaymentAccount.Detail.class})
    private Date transactionCreatedDateTime;

    @Valid
    @ManyToOne
    @JoinColumn(name="issuer_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.CreditPaymentAccount.Detail.class})
    private Issuer issuer;

    @Valid
    @ManyToOne
    @JoinColumn(name="product_id")
    @JsonView({Views.CreditPaymentAccount.Detail.class})
    private Product product;

    @Valid
    @ManyToOne
    @JoinColumn(name="payment_rule_group_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.CreditPaymentAccount.Detail.class})
    private PaymentRuleGroup paymentRuleGroup;

    @Column(name = "hirer_document")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.CreditPaymentAccount.Detail.class})
    private String hirerDocument;

    @Column(name = "service_type")
    @Enumerated(EnumType.STRING)
    @JsonView({Views.CreditPaymentAccount.List.class})
    private ServiceType serviceType;

    @Column(name = "credit_insertion_type")
    @Enumerated(EnumType.STRING)
    @JsonView({Views.CreditPaymentAccount.Detail.class})
    @NotNull(groups = {Create.class, Update.class})
    private CreditInsertionType creditInsertionType;

    @Column(name = "solicitation_date_time")
    @JsonView({Views.CreditPaymentAccount.Detail.class})
    @NotNull(groups = {Create.class, Update.class})
    private Date solicitationDateTime;

    @Column(name = "credit_number")
    @JsonView({Views.CreditPaymentAccount.Detail.class})
    @NotNull(groups = {Create.class, Update.class})
    private Long creditNumber;

    @Column(name = "insertion_created_date_time")
    @JsonView({Views.CreditPaymentAccount.Detail.class})
    @NotNull(groups = {Create.class, Update.class})
    private Date insertionCreatedDateTime;

    @Column(name = "value")
    @JsonView({Views.CreditPaymentAccount.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private BigDecimal value;

    @Column(name = "situation")
    @Enumerated(EnumType.STRING)
    @JsonView({Views.CreditPaymentAccount.Detail.class})
    @NotNull(groups = {Create.class, Update.class})
    private CreditSituation situation;

    @Column(name = "credit_source")
    @JsonView({Views.CreditPaymentAccount.Detail.class})
    @NotNull(groups = {Create.class, Update.class})
    private String creditSource;

    @Column(name = "available_balance")
    @JsonView({Views.CreditPaymentAccount.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private BigDecimal availableBalance;

    @Column(name = "payment_account_id")
    @JsonView({Views.CreditPaymentAccount.Detail.class})
    private String paymentAccount;

    @Version
    @JsonIgnore
    private Integer version;

    public void setupMyCreate(){
        insertionCreatedDateTime = new Date();
        transactionCreatedDateTime = new Date();
        solicitationDateTime = new Date();
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

    @JsonIgnore
    public String getProductIssuerId(){
        if(product != null && product.getIssuer() != null){
            return product.getIssuer().getId();
        }
        return null;
    }

    public BigDecimal getAvailableBalance(){
        if(availableBalance != null) {
            return Rounder.round(availableBalance);
        }
        return  Rounder.zero();
    }

    public void updateMyBalance(Credit credit) {
        if(credit == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(CREDIT_REQUIRED_WHEN_UPDATE_BALANCE);
        }
        if(availableBalance == null) {
            this.availableBalance = credit.getAvailableValue();
            return;
        }
        this.availableBalance = this.availableBalance.add(credit.getAvailableValue());
    }

    public boolean withProduct(){
        return  product != null;
    }

    public String getProductCode(){
        if(getProduct() != null){
            return getProduct().getCode();
        }
        return null;
    }

    public void subtract(Credit credit) {
        if(credit == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(CREDIT_REQUIRED_WHEN_SUBTRACT_BALANCE);
        }
        subtract(credit.getAvailableValue());
    }

    public void subtract(BigDecimal value) {
        if(this.availableBalance.compareTo(value) == -1){
            throw UnovationExceptions.unprocessableEntity().withErrors(VALUE_GREATER_THEN_AVAILABLE_BALANCE);
        }
        this.availableBalance = this.availableBalance.subtract(value);
    }

    public void giveBack(BigDecimal value) {
        this.availableBalance = this.availableBalance.add(value);
    }

    public void setTransactionCreatedDateTime(Date dateTime){
        this.transactionCreatedDateTime = ObjectUtils.clone(dateTime);
    }

    public Date getTransactionCreatedDateTime(){
        return ObjectUtils.clone(this.transactionCreatedDateTime);
    }

    public void setInsertionCreatedDateTime(Date dateTime){
        this.insertionCreatedDateTime = ObjectUtils.clone(dateTime);
    }

    public Date getInsertionCreatedDateTime(){
        return ObjectUtils.clone(this.insertionCreatedDateTime);
    }

    public void setSolicitationDateTime(Date dateTime){
        this.solicitationDateTime = ObjectUtils.clone(dateTime);
    }

    public Date getSolicitationDateTime(){
        return ObjectUtils.clone(this.solicitationDateTime);
    }
}
