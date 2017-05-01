package br.com.unopay.api.model;

import br.com.unopay.api.bacen.model.PaymentRuleGroup;
import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import static br.com.unopay.api.uaa.exception.Errors.*;

@Data
@Entity
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

    @Valid
    @ManyToOne
    @JoinColumn(name="product_id")
    @JsonView({Views.Public.class,Views.List.class})
    private Product product;

    @Valid
    @ManyToOne
    @JoinColumn(name="payment_rule_group_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private PaymentRuleGroup paymentRuleGroup;

    @Column(name = "hirer_document")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class,Views.List.class})
    private String hirerDocument;

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
    @NotNull(groups = {Create.class, Update.class})
    private Long creditNumber;

    @Column(name = "created_date_time")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private Date createdDateTime;

    @Column(name = "value")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private BigDecimal value;

    @Column(name = "situation")
    @Enumerated(EnumType.STRING)
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private CreditSituation situation;

    @Column(name = "credit_source")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private String creditSource;

    @Column(name = "cnab_id")
    @JsonView({Views.Public.class,Views.List.class})
    private String cnabId;

    @Column(name = "available_balance")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private BigDecimal availableBalance;

    @Column(name = "blocked_balance")
    @JsonView({Views.Public.class,Views.List.class})
    @NotNull(groups = {Create.class, Update.class})
    private BigDecimal blockedBalance;

    @Version
    @JsonIgnore
    private Integer version;

    public void validate(){
        if(product == null && paymentRuleGroup == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(PAYMENT_RULE_GROUP_OR_PRODUCT_REQUIRED);
        }
        if(product == null && creditInsertionType == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(CREDIT_INSERT_TYPE_REQUIRED);
        }
        validateCreditValue();
    }

    private void validateCreditValue() {
        if(product == null && value.compareTo(new BigDecimal(0)) == 0){
            throw UnovationExceptions.unprocessableEntity().withErrors(MINIMUM_CREDIT_VALUE_NOT_MET);
        }
        if(product != null && value.compareTo(product.getMinimumCreditInsertion()) == -1){
            throw UnovationExceptions.unprocessableEntity().withErrors(MINIMUM_PRODUCT_VALUE_NOT_MET);
        }
        if(product != null && value.compareTo(product.getMaximumCreditInsertion()) == 1){
            throw UnovationExceptions.unprocessableEntity().withErrors(MAXIMUM_PRODUCT_VALUE_NOT_MET);
        }
    }

    public void setupMyCreate(){
        if(product != null){
            paymentRuleGroup = product.getPaymentRuleGroup();
            creditInsertionType = product.getCreditInsertionType();
        }
    }

    public void incrementAvailableBalance(Optional<Credit> credit){
        if(!credit.isPresent()){
            availableBalance = this.value;
            return;
        }
        if(this.value != null) {
            availableBalance = this.value.add(credit.get().getAvailableBalance());
            return;
        }
    }

    public void incrementBlockedBalance(Optional<Credit> credit){
        if(!credit.isPresent()){
            blockedBalance = this.value;
            return;
        }
        if(this.value != null) {
            blockedBalance = this.value.add(credit.get().getBlockedBalance());
            return;
        }
    }

    public BigDecimal getAvailableBalance(){
        if(availableBalance != null) {
            return availableBalance.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        return  new BigDecimal(0).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getBlockedBalance(){
        if(blockedBalance != null) {
            return blockedBalance.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        return  new BigDecimal(0).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
