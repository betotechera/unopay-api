package br.com.unopay.api.model;

import br.com.unopay.api.bacen.model.AccreditedNetwork;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.PaymentRuleGroup;
import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import static br.com.unopay.api.uaa.exception.Errors.ACCREDITED_NETWORK_ID_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.CODE_LENGTH_NOT_ACCEPTED;
import static br.com.unopay.api.uaa.exception.Errors.ISSUER_ID_REQUIRED;
import static br.com.unopay.api.uaa.exception.Errors.PAYMENT_RULE_GROUP_ID_REQUIRED;

@Data
@Entity
@Table(name = "product")
@EqualsAndHashCode(exclude = {"issuer", "paymentRuleGroup", "accreditedNetwork"})
public class Product implements Serializable, Updatable {

    public static final long serialVersionUID = 1L;
    public static final int MAX_CODE_LENGTH = 4;

    public Product(){}

    @Id
    @Column(name="id")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @GeneratedValue(generator="system-uuid")
    private String id;

    @Column(name="code")
    @JsonView({Views.Product.List.class})
    @Size(min=1, max = 4, groups = {Create.class, Update.class})
    @NotNull(groups = {Create.class, Update.class})
    private String code;

    @Column(name="name")
    @NotNull(groups = {Create.class, Update.class})
    @Size(min=2, max = 100, groups = {Create.class, Update.class})
    private String name;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    @JsonView({Views.Product.List.class})
    private ProductType type;

    @ManyToOne
    @JoinColumn(name="issuer_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Product.List.class})
    private Issuer issuer;

    @ManyToOne
    @JoinColumn(name="payment_rule_group_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Product.Detail.class})
    private PaymentRuleGroup paymentRuleGroup;

    @ManyToOne
    @JoinColumn(name="accredited_network_id")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Product.Detail.class})
    private AccreditedNetwork accreditedNetwork;

    @Size(min = 1)
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER, targetClass = PaymentInstrumentType.class)
    @Column(name = "payment_instrument_type", nullable = false)
    @JsonView({Views.Product.Detail.class})
    @NotNull(groups = {Create.class, Update.class})
    @CollectionTable(name = "product_payment_instrument_tp", joinColumns = @JoinColumn(name = "product_id"))
    private Set<PaymentInstrumentType> paymentInstrumentTypes;

    @Size(min = 1)
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER, targetClass = ServiceType.class)
    @Column(name = "service_type", nullable = false)
    @JsonView({Views.Product.Detail.class})
    @NotNull(groups = {Create.class, Update.class})
    @CollectionTable(name = "product_service_type", joinColumns = @JoinColumn(name = "product_id"))
    private Set<ServiceType> serviceTypes;

    @Size(min = 1)
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER,targetClass = CreditInsertionType.class)
    @Column(name = "credit_insertion_type", nullable = false)
    @JsonView({Views.Product.Detail.class})
    @NotNull(groups = {Create.class, Update.class})
    @CollectionTable(name = "product_credit_insertion_type", joinColumns = @JoinColumn(name = "product_id"))
    private Set<CreditInsertionType> creditInsertionTypes;

    @Column(name = "payment_instrument_valid_days")
    @JsonView({Views.Product.Detail.class})
    @NotNull(groups = {Create.class, Update.class})
    private Integer paymentInstrumentValidDays;

    @Column(name = "situation")
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Product.List.class})
    private ProductSituation situation;

    @Column(name = "membership_fee")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Product.Detail.class})
    private BigDecimal membershipFee;

    @Column(name = "credit_insertion_fee")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Product.Detail.class})
    private BigDecimal creditInsertionFee;

    @Column(name = "pay_inst_emission_fee")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Product.Detail.class})
    private BigDecimal paymentInstrumentEmissionFee;

    @Column(name = "pay_inst_second_copy_fee")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Product.Detail.class})
    private BigDecimal paymentInstrumentSecondCopyFee;

    @Column(name = "adm_credit_insert_fee")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Product.Detail.class})
    private BigDecimal administrationCreditInsertionFee;

    @Column(name = "annuity")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Product.Detail.class})
    private BigDecimal annuity;

    @Column(name = "payment_installments")
    @JsonView({Views.Product.Detail.class})
    private Integer paymentInstallments;

    @Min(1)
    @Column(name = "contract_validity_days")
    @JsonView({Views.Product.Detail.class})
    private Integer contractValidityDays;

    @Version
    @JsonIgnore
    private Integer version;

    public void validate(){
        if(getCode().length() > MAX_CODE_LENGTH){
            throw UnovationExceptions.unprocessableEntity().withErrors(CODE_LENGTH_NOT_ACCEPTED);
        }
        if(getIssuer() != null && getIssuer().getId() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(ISSUER_ID_REQUIRED);
        }
        if(getAccreditedNetwork() != null && getAccreditedNetwork().getId() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(ACCREDITED_NETWORK_ID_REQUIRED);
        }
        if(getPaymentRuleGroup() != null && getPaymentRuleGroup().getId() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(PAYMENT_RULE_GROUP_ID_REQUIRED);
        }
    }

    public void validateCreditInsertionType(CreditInsertionType creditInsertionType){
        if(!this.creditInsertionTypes.contains(creditInsertionType)){
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.CREDIT_INSERTION_TYPE_NOT_IN_PRODUCT);
        }
    }

}
